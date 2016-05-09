package fands.support.runner;

import fands.model.DiscList;
import fands.model.disc.*;
import fands.service.DiscService;
import fands.spdier.*;
import fands.support.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static fands.support.HelpUtil.nullSafeGet;

@Service
public class AutoRunner {

    private static final String SAKURA_SPEED_URL = "http://rankstker.net/index_news.cgi";
    private static final String AMAZON_SPEED_URL = "http://www.amazon.co.jp/gp/bestsellers/dvd/ref=zg_bs_dvd_pg_%d?ie=UTF8&pg=%d";
    private static final String SAKURA_DISC_URL = "http://rankstker.net/show.cgi?n=";
    private static final String AMAZON_DISC_URL = "http://www.amazon.co.jp/dp/";

    private Logger logger = LogManager.getLogger(AutoRunner.class);

    private DiscService discService;
    private SpiderService spiderService;

    private SakuraSpeedSpider sakuraSpeedSpider;
    private AmazonSpeedSpider amazonSpeedSpider;
    private SakuraDiscSpider sakuraDiscSpider;
    private AmazonDiscSpider amazonDiscSpider;

    @PostConstruct
    public void startAutoRunner() {
        /**
         * 任务添加线程
         */
        newTimerSchedule("Sakura速报", 5, 60, this::addUpdateSakuraSpeedTask);
//        newTimerSchedule("Sakura碟片", 10, 240, () -> addUpdateSakuraHotTask(120));
//        newTimerSchedule("Sakura碟片", 12, 900, () -> addUpdateSakuraExtTask(450));
//        newTimerSchedule("Sakura碟片", 14, 1800, () -> addUpdateSakuraAllTask(900));
        newTimerSchedule("Amazon速报", 16, 60, this::addUpdateAmazonSpeedTask);
        newTimerSchedule("Amazon碟片", 18, 60, () -> addUpdateAmazonHotTask(30));
        newTimerSchedule("Amazon碟片", 20, 180, () -> addUpdateAmazonExtTask(90));
        newTimerSchedule("Amazon碟片", 22, 1800, () -> addUpdateAmazonAllTask(900));
    }

    private void addUpdateSakuraSpeedTask() {
        logger.info("开始更新Sakura速报数据");
        spiderService.getSakura1().add(new SpiderTask(SAKURA_SPEED_URL, () -> true, document -> {
            sakuraSpeedSpider.parseDocument(document);
            logger.info("成功更新Sakura速报数据");
        }));
    }

    private void addUpdateAmazonSpeedTask() {
        for (int page = 1; page <= 5; page++) {
            int start = page * 20 - 19;
            logger.printf(Level.INFO, "开始更新Amazon速报数据(%02d - %02d)", start, start + 19);
            String amazon_url = String.format(AMAZON_SPEED_URL, page, page);
            spiderService.getAmazon1().add(new SpiderTask(amazon_url, () -> true, document -> {
                amazonSpeedSpider.parseDoucment(document);
                logger.printf(Level.INFO, "成功更新Amazon速报数据(%02d - %02d)", start, start + 19);
            }));
        }
    }

    private void addUpdateSakuraHotTask(int second) {
        DiscList discList = discService.getLatestDiscList();
        discList.setDiscs(discService.getDiscsOfDiscList(discList, 10));
        addSakuraDiscTask("Sakura(Hot)", second, spiderService.getSakura2(), discList);
    }

    private void addUpdateAmazonHotTask(int second) {
        DiscList discList = discService.getLatestDiscList();
        discList.setDiscs(discService.getDiscsOfDiscList(discList, 10));
        addAmazonDiscTask("Amazon(Hot)", second, spiderService.getAmazon2(), discList);
    }

    private void addUpdateSakuraExtTask(int second) {
        discService.findLatestDiscList().forEach(discList -> {
            discList.setDiscs(discService.getDiscsOfDiscList(discList));
            addSakuraDiscTask("Sakura(Ext)", second, spiderService.getSakura3(), discList);
        });
    }

    private void addUpdateAmazonExtTask(int second) {
        discService.findLatestDiscList().forEach(discList -> {
            discList.setDiscs(discService.getDiscsOfDiscList(discList));
            addAmazonDiscTask("Amazon(Ext)", second, spiderService.getAmazon3(), discList);
        });
    }

    private void addUpdateSakuraAllTask(int second) {
        DiscList discList = Constants.ALL_DISCS;
        discList.setDiscs(discService.findAllDiscs());
        addSakuraDiscTask("Sakura(All)", second, spiderService.getSakura4(), discList);
    }

    private void addUpdateAmazonAllTask(int second) {
        DiscList discList = Constants.ALL_DISCS;
        discList.setDiscs(discService.findAllDiscs());
        addAmazonDiscTask("Amazon(All)", second, spiderService.getAmazon4(), discList);
    }

    private void addSakuraDiscTask(String name, int second, List<SpiderTask> taskList, DiscList discList) {
        AtomicInteger count = new AtomicInteger(discList.getDiscs().size());
        loggerStartUpdate(name, discList, count);
        discList.getDiscs().forEach(disc -> {
            String url = SAKURA_DISC_URL + disc.getAsin();
            taskList.add(new SpiderTask(url, needUpdateSakura(disc, second), document -> {
                if (document != null) {
                    sakuraDiscSpider.parseDocument(disc, document);
                }
                loggerDiscUpdate(name, discList, disc, count, document == null);
            }));
        });
    }

    private void addAmazonDiscTask(String name, int second, List<SpiderTask> taskList, DiscList discList) {
        AtomicInteger count = new AtomicInteger(discList.getDiscs().size());
        loggerStartUpdate(name, discList, count);
        discList.getDiscs().forEach(disc -> {
            String url = AMAZON_DISC_URL + disc.getAsin();
            taskList.add(new SpiderTask(url, needUpdateAmazon(disc, second), document -> {
                if (document != null) {
                    amazonDiscSpider.parseDocument(disc, document);
                }
                loggerDiscUpdate(name, discList, disc, count, document == null);
            }));
        });
    }

    private Supplier<Boolean> needUpdateSakura(Disc disc, int second) {
        return () -> isNeedUpdate(nullSafeGet(disc.getSakura(), DiscSakura::getPadt), second);
    }

    private Supplier<Boolean> needUpdateAmazon(Disc disc, int second) {
        return () -> isNeedUpdate(nullSafeGet(disc.getAmazon(), DiscAmazon::getPadt), second);
    }

    private boolean isNeedUpdate(Date date, int second) {
        return date == null || date.compareTo(DateUtils.addSeconds(new Date(), -second)) < 0;
    }

    private void loggerStartUpdate(String name, DiscList discList, AtomicInteger count) {
        logger.printf(Level.INFO, "开始更新%s碟片数据(%s), 总共%d个", name, discList.getTitle(), count.get());
    }

    private void loggerDiscUpdate(String name, DiscList discList, Disc disc, AtomicInteger count, boolean skip) {
        debugDiscUpdate(name, discList, disc, count, skip);
        if (count.get() == 0) {
            logger.printf(Level.INFO, "成功更新%s碟片数据(%s)", name, discList.getTitle());
        } else if (count.get() % 10 == 0) {
            logger.printf(Level.INFO, "正在更新%s碟片数据(%s), 还剩%d个未更新", name, discList.getTitle(), count.get());
        }
    }

    private void debugDiscUpdate(String name, DiscList discList, Disc disc, AtomicInteger count, boolean skip) {
        logger.printf(Level.DEBUG, "%s更新%s碟片数据(%s)[%s], 还剩%d个未更新", skip ? "跳过" : "正在",
                name, discList.getTitle(), disc.getAsin(), count.decrementAndGet());
    }

    private void newTimerSchedule(String name, long delay, long period, Runnable runnable) {
        logger.printf(Level.INFO, "已计划抓取%s数据, 每 %d 秒运行一次", name, period);
        new Timer(true).schedule(new TimerTask() {
            public void run() {
                runnable.run();
                spiderService.nodifyWaitObject();
            }
        }, delay * 1000, period * 1000);
    }

    @Autowired
    public void setDiscService(DiscService discService) {
        this.discService = discService;
    }

    @Autowired
    public void setSpiderService(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    @Autowired
    public void setSakuraSpeedSpider(SakuraSpeedSpider sakuraSpeedSpider) {
        this.sakuraSpeedSpider = sakuraSpeedSpider;
    }

    @Autowired
    public void setAmazonSpeedSpider(AmazonSpeedSpider amazonSpeedSpider) {
        this.amazonSpeedSpider = amazonSpeedSpider;
    }

    @Autowired
    public void setSakuraDiscSpider(SakuraDiscSpider sakuraDiscSpider) {
        this.sakuraDiscSpider = sakuraDiscSpider;
    }

    @Autowired
    public void setAmazonDiscSpider(AmazonDiscSpider amazonDiscSpider) {
        this.amazonDiscSpider = amazonDiscSpider;
    }

}
