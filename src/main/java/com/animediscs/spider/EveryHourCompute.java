package com.animediscs.spider;

import com.animediscs.action.RankAction;
import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.animediscs.util.Helper.getSday;

@Service
public class EveryHourCompute {

    private Logger logger = LogManager.getLogger(EveryHourCompute.class);

    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doCompute(ExecutorService execute) throws Exception {
        Set<Disc> sakuraList = new LinkedHashSet<>();
        Set<Disc> needRecord = new LinkedHashSet<>();
        Set<Disc> computeList = new LinkedHashSet<>();
        dao.execute(session -> {
            dao.findBy(DiscList.class, "sakura", true)
                    .stream().map(DiscList::getDiscs)
                    .forEach(sakuraList::addAll);
            dao.findAll(DiscList.class)
                    .stream().map(DiscList::getDiscs)
                    .forEach(needRecord::addAll);
            computeList.addAll(dao.findBy(Disc.class, "type", DiscType.CD));
            computeList.addAll(dao.lookup(DiscList.class, "name", "shiqiu").getDiscs());
            computeList.addAll(dao.lookup(DiscList.class, "name", "saenai").getDiscs());
        });
        logger.printf(Level.INFO, "正在计算PT, 共%d个", computeList.size());
        computeList.forEach(disc -> {
            execute.execute(() -> {
                DiscSakura sakura = disc.getSakura();
                if (sakura != null && sakura.getSday() >= -1) {
                    dao.refresh(sakura);
                    sakura.setSday(getSday(disc));
                    sakura.setCupt(getCupt(disc));
                    logger.printf(Level.INFO, "正在计算PT:「%s」->(%d pt)",
                            disc.getTitle(), sakura.getCupt());
                    dao.saveOrUpdate(sakura);
                }
            });
        });
        logger.printf(Level.INFO, "正在清除其他碟片PT");
        dao.findAll(Disc.class).stream()
                .filter(disc -> !sakuraList.contains(disc))
                .filter(disc -> !computeList.contains(disc))
                .forEach(disc -> {
                    DiscSakura sakura = disc.getSakura();
                    if (sakura != null && sakura.getSday() >= -1) {
                        dao.refresh(sakura);
                        sakura.setSday(getSday(disc));
                        sakura.setCupt(0);
                        logger.printf(Level.INFO, "正在清除PT:「%s」->(%d pt)",
                                disc.getTitle(), sakura.getCupt());
                        dao.saveOrUpdate(sakura);
                    }
                });
        logger.printf(Level.INFO, "正在清理过期的排名记录");
        dao.findAll(Disc.class).stream()
                .filter(disc -> !needRecord.contains(disc))
                .filter(disc -> getSday(disc) < -10)
                .forEach(disc -> {
                    logger.printf(Level.INFO, "正在清除排名:「%s」",
                            disc.getTitle());
                    dao.findBy(DiscRecord.class, "disc", disc)
                            .forEach(dao::delete);
                });
    }

    private int getCupt(Disc disc) {
        switch (disc.getType()) {
            case CD:
                return getCdCupt(disc);
            case OTHER:
                return 0;
            default:
                return getDvdCupt(disc);
        }
    }

    private int getCdCupt(Disc disc) {
        return (int) (0.5 + dao.query(session -> {
            List<DiscRecord> records = RankAction.getRecords(disc, session);
            return RankAction.computeRecordsPtOfCd(disc, records).stream()
                    .mapToDouble(DiscRecord::getCupt)
                    .findFirst().orElse(0);
        }));
    }

    private int getDvdCupt(Disc disc) {
        return (int) (0.5 + dao.query(session -> {
            List<DiscRecord> records = RankAction.getRecords(disc, session);
            return RankAction.computeRecordsPtOfDvd(disc, records).stream()
                    .mapToDouble(DiscRecord::getCupt)
                    .findFirst().orElse(0);
        }));
    }

}
