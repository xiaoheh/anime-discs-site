package com.animediscs.spider;

import com.animediscs.action.RankAction;
import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        Set<Disc> discs = new LinkedHashSet<>();
        dao.execute(session -> {
            dao.lookup(DiscList.class, "name", "mydvd")
                    .getDiscs()
                    .forEach(discs::add);
            dao.findBy(Disc.class, "type", DiscType.CD)
                    .forEach(discs::add);
        });
        logger.printf(Level.INFO, "正在计算PT, 共%d个", discs.size());
        discs.forEach(disc -> {
            execute.execute(() -> {
                DiscSakura sakura = disc.getSakura();
                if (sakura == null) {
                    sakura = new DiscSakura();
                    sakura.setDisc(disc);
                } else {
                    dao.refresh(sakura);
                }
                sakura.setCupt(getCupt(disc));
                sakura.setSday(getSday(disc));
                dao.saveOrUpdate(sakura);
                logger.printf(Level.INFO, "正在计算PT, %s->%d pt",
                        disc.getTitle(), sakura.getCupt());
            });
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
