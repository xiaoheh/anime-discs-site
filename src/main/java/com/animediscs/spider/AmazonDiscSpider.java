package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.service.DiscService;
import com.animediscs.support.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.animediscs.util.Helper.nullSafeGet;
import static com.animediscs.util.Parser.parseNumber;

@Service
public class AmazonDiscSpider {

    private Logger logger = LogManager.getLogger(AmazonDiscSpider.class);
    private Pattern pattern = Pattern.compile("DVD \\- ([0-9,]+)位");

    private Dao dao;
    private DiscService discService;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setDiscService(DiscService discService) {
        this.discService = discService;
    }

    public void doUpdateHot(int second, SpiderService service, int level) {
        DiscList discList = discService.getLatestDiscList();
        if (discList != null) {
            discList.setDiscs(discService.getDiscsOfDiscList(discList, 15));
            addUpdateTask("Amazon(Hot)", second, service, level, discList);
        }
    }

    public void doUpdateExt(int second, SpiderService service, int level) {
        discService.findLatestDiscExtList().forEach(discList -> {
            discList.setDiscs(discService.getDiscsOfDiscList(discList));
            addUpdateTask("Amazon(Ext)", second, service, level, discList);
        });
    }

    public void doUpdateAll(int second, SpiderService service, int level) {
        DiscList discList = Constants.ALL_DISCS;
        discList.setDiscs(discService.findAllDiscs());
        addUpdateTask("Amazon(All)", second, service, level, discList);
    }

    private void addUpdateTask(String name, int second, SpiderService service, int level, DiscList discList) {
        AtomicInteger skip = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(discList.getDiscs().size());
        AtomicInteger update = new AtomicInteger(0);
        infoStart(name, discList, count);
        discList.getDiscs().forEach(disc -> {
            String url = "http://www.amazon.co.jp/dp/" + disc.getAsin();
            service.addTask(level, url, needUpdate(disc, second), document -> {
                if (document != null) {
                    updateDiscAmazon(disc, document);
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

    private void updateDiscAmazon(Disc disc, Document document) {
        DiscRank discRank = getDiscAmazon(disc);
        if (discRank.getId() != null) {
            dao.refresh(discRank);
        }
        String text = document.select("#SalesRank").text();
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            int curk = parseNumber(matcher.group(1));
            discRank.setPadt(new Date());
            discRank.setPark(curk);
            if (discRank.getPark() != discRank.getPark1()) {
                pushRank(discRank);
            }
        } else {
            logger.printf(Level.DEBUG, "未找到Amazon排名数据, 跳过此碟片: %s", disc.getAsin());
        }
        dao.saveOrUpdate(discRank);
    }

    private void pushRank(DiscRank discRank) {
        discRank.setPadt5(discRank.getPadt4());
        discRank.setPadt4(discRank.getPadt3());
        discRank.setPadt3(discRank.getPadt2());
        discRank.setPadt2(discRank.getPadt1());
        discRank.setPadt1(discRank.getPadt());
        discRank.setPark5(discRank.getPark4());
        discRank.setPark4(discRank.getPark3());
        discRank.setPark3(discRank.getPark2());
        discRank.setPark2(discRank.getPark1());
        discRank.setPark1(discRank.getPark());
    }

    private DiscRank getDiscAmazon(Disc disc) {
        DiscRank discRank = disc.getRank();
        if (discRank == null) {
            discRank = new DiscRank();
            discRank.setDisc(disc);
        }
        return discRank;
    }

    private Supplier<Boolean> needUpdate(Disc disc, int second) {
        return () -> {
            Date date = nullSafeGet(disc.getRank(), DiscRank::getPadt);
            return date == null || date.compareTo(DateUtils.addSeconds(new Date(), -second)) < 0;
        };
    }

}
