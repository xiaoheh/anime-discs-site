package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.runner.task.DiscSpiderTask;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import static com.animediscs.model.Disc.*;
import static com.animediscs.util.Helper.*;
import static com.animediscs.util.Parser.*;

@Service
public class AmazonRankSpider {

    private Logger logger = LogManager.getLogger(AmazonRankSpider.class);

    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdateHot(SpiderService service, int level) {
        if (service.isBusy(level)) {
            logger.printf(Level.INFO, "抓取服务忙, 暂停添加任务");
            return;
        }
        List<Disc> discs = new LinkedList<>();
        dao.execute(session -> {
            findLatestSakura(session)
                    .setFirstResult(1)
                    .list().forEach(o -> {
                DiscList discList = (DiscList) o;
                discList.getDiscs().stream()
                        .sorted(sortBySakura()).limit(10)
                        .forEach(discs::add);
            });
        });
        AtomicBoolean needUpdate = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(discs.size());
        infoUpdateStart("Amazon(Hot)", "近期新番", count);
        discs.forEach(disc -> {
            Supplier<Boolean> test = needUpdate(disc, 45);
            service.addTask(level, new DiscSpiderTask(disc.getAsin(), test, document -> {
                if (updateRank(disc, document, count, Level.INFO)) {
                    needUpdate.set(true);
                }
                if (count.get() == 0) {
                    infoUpdateFinish("Amazon(Hot)", "近期新番");
                    if (needUpdate.get()) {
                        logger.info("发现排名有变化, 准备更新全部排名数据");
                        doUpdateAll(service, level + 1);
                    }
                }
            }));
        });
    }

    public void doUpdateAll(SpiderService service, int level) {
        if (service.isBusy(level)) {
            logger.printf(Level.INFO, "抓取服务忙, 暂停添加任务");
            return;
        }
        Set<Disc> discs = new LinkedHashSet<>();
        dao.execute(session -> {
            Set<Disc> later = new LinkedHashSet<>();
            findLatestSakura(session).list().forEach(o -> {
                DiscList discList = (DiscList) o;
                discList.getDiscs().stream()
                        .sorted(sortBySakura())
                        .limit(30)
                        .forEach(discs::add);
                discList.getDiscs().stream()
                        .sorted(sortBySakura())
                        .skip(30)
                        .forEach(later::add);
            });
            Builder<String> builder = Stream.builder();
            builder.add("myfav");
            builder.add("llss");
            builder.add("rezero");
            builder.add("kabaneri");
            builder.add("macross");
            builder.add("haifuri");
            builder.add("mydvd");
            builder.add("mycd");
            builder.build().forEach(name -> {
                DiscList discList = dao.lookup(DiscList.class, "name", name);
                if (discList != null) {
                    discList.getDiscs().stream()
                            .sorted(sortByAmazon())
                            .forEach(discs::add);
                }
            });
            findNotSakura(session).list().forEach(o -> {
                DiscList discList = (DiscList) o;
                discList.getDiscs().stream()
                        .sorted(sortByAmazon())
                        .forEach(later::add);
            });
            later.forEach(discs::add);
        });
        dao.findAll(Disc.class).stream()
                .filter(disc -> getSday(disc) >= -7)
                .forEach(discs::add);
        dao.findAll(Disc.class).stream()
                .filter(disc -> disc.getRank() == null || disc.getRank().getPark() <= 200)
                .forEach(discs::add);
        AtomicBoolean needUpdate = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(discs.size());
        infoUpdateStart("Amazon(All)", "所有碟片", count);
        discs.forEach(disc -> {
            Supplier<Boolean> test = needUpdate(disc, 30);
            service.addTask(level, new DiscSpiderTask(disc.getAsin(), test, document -> {
                if (updateRank(disc, document, count, Level.INFO)) {
                    needUpdate.set(true);
                }
                if (count.get() == 0) {
                    infoUpdateFinish("Amazon(All)", "所有碟片");
                    if (needUpdate.get()) {
                        logger.info("发现排名有变化, 准备再次更新排名数据");
                        doUpdateAll(service, level);
                    }
                }
            }));
        });
    }

    private Criteria findLatestSakura(Session session) {
        Date yesterday = DateUtils.addDays(new Date(), -1);
        return session.createCriteria(DiscList.class)
                .add(Restrictions.eq("sakura", true))
                .add(Restrictions.gt("date", yesterday))
                .addOrder(Order.desc("name"));
    }

    private Criteria findNotSakura(Session session) {
        return session.createCriteria(DiscList.class)
                .add(Restrictions.eq("sakura", false))
                .addOrder(Order.desc("name"));
    }

    private Supplier<Boolean> needUpdate(Disc disc, int minute) {
        return () -> {
            DiscRank rank = disc.getRank();
            if (rank != null) {
                dao.refresh(rank);
            }
            return needUpdate(nullSafeGet(rank, DiscRank::getPadt1), minute);
        };
    }

    private boolean updateRank(Disc disc, Document document, AtomicInteger count, Level level) {
        if (document != null) {
            Node rankNode = getNode(document, "SalesRank");
            if (rankNode != null) {
                if (updateRank(disc, rankNode)) {
                    loggerRankChange(level, disc, count);
                    return true;
                } else {
                    loggerRankUnChange(level, disc, count);
                }
            } else {
                loggerNoRank(level, disc, count);
            }
            updateDiscInfo(disc, document);
        } else {
            loggerSkipUpdate(level, disc, count);
        }
        return false;
    }

    private void updateDiscInfo(Disc disc, Document document) {
        dao.refresh(disc);
        Document items = getNode(document, "ItemAttributes").getOwnerDocument();
        disc.setJapan(getValue(items, "Title"));
        setRelease(disc, getValue(items, "ReleaseDate"));
        dao.update(disc);
    }

    private void setRelease(Disc disc, String dateText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        disc.setRelease(parseDate(sdf, dateText));
    }

    private Node getNode(Document document, String itemAttributes) {
        return document.getElementsByTagName(itemAttributes).item(0);
    }

    private String getValue(Document items, String title) {
        return getValue(getNode(items, title));
    }

    private String getValue(Node rank) {
        return rank.getTextContent();
    }

    private boolean updateRank(Disc disc, Node rankNode) {
        boolean rankChanged = false;
        DiscRank discRank = getDiscRank(disc);
        if (needUpdate(discRank.getPadt1(), 20)) {
            discRank.setPark(parseNumber(rankNode.getTextContent()));
            discRank.setPadt(new Date());
            if (discRank.getPark() != discRank.getPark1()) {
                pushRank(discRank);
                saveRank(discRank);
                updateSakura(disc);
                rankChanged = true;
            }
            dao.saveOrUpdate(discRank);
        }
        return rankChanged;
    }

    private void updateSakura(Disc disc) {
        DiscSakura sakura = disc.getSakura();
        DiscRank rank = disc.getRank();
        if (sakura != null) {
            dao.refresh(sakura);
        } else {
            sakura = new DiscSakura();
            sakura.setDisc(disc);
        }
        sakura.setDate(rank.getPadt1());
        sakura.setCurk(rank.getPark1());
        sakura.setPrrk(rank.getPark2());
        sakura.setSday(getSday(disc));
        dao.saveOrUpdate(sakura);
    }

    private boolean needUpdate(Date date, int minute) {
        Date twentyMintue = DateUtils.addMinutes(new Date(), -minute);
        return date == null || date.compareTo(twentyMintue) < 0;
    }

    private void infoUpdateStart(String name, String title, AtomicInteger count) {
        logger.printf(Level.INFO, "开始更新%s排名数据(%s), 总共%d个", name, title, count.get());
    }

    private void infoUpdateFinish(String name, String title) {
        logger.printf(Level.INFO, "成功更新%s排名数据(%s)", name, title);
    }

    private void loggerSkipUpdate(Level level, Disc disc, AtomicInteger count) {
        logger.printf(level, "排名数据不需更新, ASIN=%s, Rank=%d, Title=%s, 还剩%d个未更新",
                disc.getAsin(), disc.getRank().getPark1(), disc.getTitle(), count.decrementAndGet());
    }

    private void loggerRankChange(Level level, Disc disc, AtomicInteger count) {
        logger.printf(level, "排名数据发生变化, ASIN=%s, Rank=%d->%d, Title=%s, 还剩%d个未更新",
                disc.getAsin(), disc.getRank().getPark2(), disc.getRank().getPark1(), disc.getTitle(), count.decrementAndGet());
    }

    private void loggerRankUnChange(Level level, Disc disc, AtomicInteger count) {
        logger.printf(level, "排名数据保持不变, ASIN=%s, Rank=%d, Title=%s, 还剩%d个未更新",
                disc.getAsin(), disc.getRank().getPark1(), disc.getTitle(), count.decrementAndGet());
    }

    private void loggerNoRank(Level level, Disc disc, AtomicInteger count) {
        logger.printf(level, "未找到排名数据, ASIN=%s, Title=%s, 还剩%d个未更新",
                disc.getAsin(), disc.getTitle(), count.decrementAndGet());
    }

    private DiscRank getDiscRank(Disc disc) {
        DiscRank rank = disc.getRank();
        if (rank == null) {
            rank = new DiscRank();
            rank.setDisc(disc);
            disc.setRank(rank);
        } else {
            dao.refresh(rank);
        }
        return rank;
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

}
