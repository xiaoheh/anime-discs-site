package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.animediscs.util.Helper.nullSafeGet;
import static com.animediscs.util.Parser.parseNumber;
import static java.lang.System.currentTimeMillis;

@Service
public class AmazonDiscSpider {

    private Logger logger = LogManager.getLogger(AmazonDiscSpider.class);
    private Pattern pattern = Pattern.compile("- ([0-9,]+)位");

    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdateHot(int second, SpiderService service, int level) {
        dao.execute(session -> {
            Date yesterday = DateUtils.addDays(new Date(), -1);
            session.createCriteria(DiscList.class)
                    .add(Restrictions.eq("sakura", true))
                    .add(Restrictions.gt("date", yesterday))
                    .addOrder(Order.desc("name"))
                    .setFirstResult(1)
                    .setMaxResults(1)
                    .list().forEach(o -> {
                DiscList discList = (DiscList) o;
                List<Disc> discs = discList.getDiscs().stream()
                        .sorted(Disc.sortBySakura()).limit(15)
                        .collect(Collectors.toList());
                addUpdateTask("Amazon(Hot)", second, service, level, discList, discs);
            });
        });
    }

    public void doUpdateExt(int second, SpiderService service, int level) {
        if (service.isBusy(level)) {
            logger.printf(Level.INFO, "抓取服务忙, 暂停添加任务");
            return;
        }
        dao.execute(session -> {
            Date yesterday = DateUtils.addDays(new Date(), -1);
            session.createCriteria(DiscList.class)
                    .add(Restrictions.eq("sakura", true))
                    .add(Restrictions.gt("date", yesterday))
                    .addOrder(Order.desc("name"))
                    .setFirstResult(1)
                    .list().forEach(o -> {
                DiscList discList = (DiscList) o;
                discList.getDiscs().sort(Disc.sortBySakura());
                addUpdateTask("Amazon(Ext)", second, service, level, discList);
            });
        });
    }

    public void doUpdateAll(int second, SpiderService service, int level) {
        if (service.isBusy(level)) {
            logger.printf(Level.INFO, "抓取服务忙, 暂停添加任务");
            return;
        }
        DiscList discList = new DiscList();
        discList.setName("all_disc");
        discList.setTitle("全部碟片");
        discList.setDiscs(dao.findAll(Disc.class));
        addUpdateTask("Amazon(All)", second, service, level, discList);
    }

    private void addUpdateTask(String name, int second, SpiderService service, int level, DiscList discList) {
        addUpdateTask(name, second, service, level, discList, discList.getDiscs());
    }

    private void addUpdateTask(String name, int second, SpiderService service, int level, DiscList discList, List<Disc> discs) {
        AtomicInteger skip = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(discs.size());
        AtomicInteger update = new AtomicInteger(0);
        infoStart(name, discList, count);
        discs.stream().sorted(Disc.sortBySakura()).forEach(disc -> {
            String url = "http://www.amazon.co.jp/dp/" + disc.getAsin();
            service.addTask(level, url, needUpdate(disc, second), document -> {
                if (document != null) {
                    String rankText = document.select("#SalesRank").text();
                    updateRank(getDiscRank(disc), rankText);
                    debugUpdate(name, discList, count, disc, update);
                } else {
                    debugSkip(name, discList, count, disc, skip);
                }
                if (count.get() == 0) {
                    infoFinish(name, discList, skip, update);
                } else if (count.get() % 10 == 0) {
                    infoUpdateTen(name, discList, count);
                }
            });
        });
    }

    private DiscRank getDiscRank(Disc disc) {
        DiscRank rank = disc.getRank();
        if (rank == null) {
            rank = new DiscRank();
            rank.setDisc(disc);
        } else {
            dao.refresh(rank);
        }
        return rank;
    }

    private void infoStart(String name, DiscList discList, AtomicInteger count) {
        logger.printf(Level.INFO, "开始更新%s碟片数据(%s), 总共%d个",
                name, discList.getTitle(), count.get());
    }

    private void debugUpdate(String name, DiscList discList, AtomicInteger count, Disc disc, AtomicInteger update) {
        update.incrementAndGet();
        logger.printf(Level.DEBUG, "正在更新%s碟片数据(%s)[%s], 还剩%d个未更新",
                name, discList.getTitle(), disc.getAsin(), count.decrementAndGet());
    }

    private void debugSkip(String name, DiscList discList, AtomicInteger count, Disc disc, AtomicInteger skip) {
        skip.incrementAndGet();
        logger.printf(Level.DEBUG, "跳过更新%s碟片数据(%s)[%s], 还剩%d个未更新",
                name, discList.getTitle(), disc.getAsin(), count.decrementAndGet());
    }

    private void infoUpdateTen(String name, DiscList discList, AtomicInteger count) {
        logger.printf(Level.INFO, "正在更新%s碟片数据(%s), 还剩%d个未更新",
                name, discList.getTitle(), count.get());
    }

    private void infoFinish(String name, DiscList discList, AtomicInteger skip, AtomicInteger update) {
        logger.printf(Level.INFO, "成功更新%s碟片数据(%s), 更新%d个, 跳过%d个",
                name, discList.getTitle(), update.get(), skip.get());
    }

    private void updateRank(DiscRank rank, String rankText) {
        rank.setPadt(new Date());
        if (needUpdate(rank.getPadt1())) {
            Matcher matcher = pattern.matcher(rankText);
            if (matcher.find()) {
                rank.setPark(parseNumber(matcher.group(1)));
                if (rank.getPark() != rank.getPark1()) {
                    pushRank(rank);
                    saveRank(rank);
                }
                dao.saveOrUpdate(rank);
            }
        }
    }

    private void pushRank(DiscRank rank) {
        rank.setPadt5(rank.getPadt4());
        rank.setPadt4(rank.getPadt3());
        rank.setPadt3(rank.getPadt2());
        rank.setPadt2(rank.getPadt1());
        rank.setPadt1(rank.getPadt());
        rank.setPark5(rank.getPark4());
        rank.setPark4(rank.getPark3());
        rank.setPark3(rank.getPark2());
        rank.setPark2(rank.getPark1());
        rank.setPark1(rank.getPark());
    }

    private void saveRank(DiscRank rank) {
        DiscRecord record = new DiscRecord();
        record.setDisc(rank.getDisc());
        record.setDate(rank.getPadt());
        record.setRank(rank.getPark());
        dao.save(record);
    }

    private boolean needUpdate(Date date) {
        return date == null || date.getTime() < currentTimeMillis() - 300000;
    }

    private Supplier<Boolean> needUpdate(Disc disc, int second) {
        return () -> {
            Date date = nullSafeGet(disc.getRank(), DiscRank::getPadt);
            return date == null || date.compareTo(DateUtils.addSeconds(new Date(), -second)) < 0;
        };
    }

}
