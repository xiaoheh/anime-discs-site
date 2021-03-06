package com.animediscs.runner;

import com.animediscs.spider.*;
import com.animediscs.util.Format;
import com.animediscs.util.Helper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AutoRunner {

    private Logger logger = LogManager.getLogger(AutoRunner.class);
    private long startupTimeMillis = System.currentTimeMillis();
    private ExecutorService execute;

    private SpiderService sakuraRunner;
    private SpiderService amazonRunner;
    private SpiderService rankerRunner;

    private SakuraSpeedSpider sakuraSpeedSpider;
    private AmazonSpeedSpider amazonSpeedSpider;
    private AmazonAnimeSpider amazonAnimeSpider;
    private AmazonRankSpider amazonRankSpider;
    private EveryHourCompute everyHourCompute;

    public AutoRunner() {
        int accountCount = getAccountCount();
        execute = Executors.newFixedThreadPool(1);
        sakuraRunner = new SpiderService("Sakura", 1, 1, execute);
        amazonRunner = new SpiderService("Amazon", 2, 1, execute);
        rankerRunner = new SpiderService("Ranker", 4, accountCount, execute);
    }

    private int getAccountCount() {
        Properties properties = Helper.loadProperties("config/setting.properties");
        return Integer.parseInt(properties.getProperty("amazon.account.count"));
    }

    @PostConstruct
    public void startAutoRunner() {
        schedule("Sakura速报数据抓取", 5, 60, () -> {
            sakuraSpeedSpider.doUpdate(sakuraRunner, 1);
        });
        schedule("Amazon速报数据抓取", 10, 120, () -> {
            amazonSpeedSpider.doUpdate(amazonRunner, 1);
        });
//        schedule("Amazon动画数据抓取", 15, 3600, () -> {
//            amazonAnimeSpider.doUpdate(amazonRunner, 2);
//        });
        schedule("Amazon排名数据抓取", 20, 120, () -> {
            amazonRankSpider.doUpdateHot(rankerRunner, 2);
        });
        schedule("Amazon排名数据抓取", 25, 1200, () -> {
            amazonRankSpider.doUpdateAll(rankerRunner, 4);
        });
        everHour("每小时计算任务", () -> {
            everyHourCompute.doCompute(execute);
        });
        schedule("任务线程状态报告", 0, 30, () -> {
            String timeout = Format.formatTimeout(startupTimeMillis);
            logger.printf(Level.INFO, "(%s): %s", timeout, sakuraRunner.getStatus());
            logger.printf(Level.INFO, "(%s): %s", timeout, amazonRunner.getStatus());
            logger.printf(Level.INFO, "(%s): %s", timeout, rankerRunner.getStatus());
        });
    }

    private void everHour(String name, Callable callable) {
        logger.printf(Level.INFO, "已添加计划任务 %s, 下个小时01分开始运行, 每小时运行一次", name);
        LocalDateTime dateTime = LocalDateTime.now();
        if (dateTime.getNano() != 0 || dateTime.getSecond() != 0 || dateTime.getMinute() != 1) {
            dateTime = dateTime.withNano(0).withSecond(0).withMinute(1).plusHours(1);
        }
        Date firstTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                invokeCallable(callable, name);
            }
        }, firstTime, 3600000);
        timer.schedule(new TimerTask() {
            public void run() {
                invokeCallable(callable, name);
            }
        }, 60000);
    }

    private void schedule(String name, long delay, long period, Callable callable) {
        logger.printf(Level.INFO, "已添加计划任务 %s, %d秒后开始运行, 每%d秒运行一次", name, delay, period);
        new Timer(true).schedule(new TimerTask() {
            public void run() {
                invokeCallable(callable, name);
            }
        }, delay * 1000, period * 1000);
    }

    private void invokeCallable(Callable callable, String name) {
        try {
            callable.call();
        } catch (Exception e) {
            logger.printf(Level.WARN, "计划任务 %s 出现错误: %s: %s",
                    name, e.getClass().getSimpleName(), e.getMessage());
            logger.catching(Level.DEBUG, e);
        }
    }

    public SpiderService getRankerRunner() {
        return rankerRunner;
    }

    private interface Callable {
        void call() throws Exception;
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
    public void setAmazonAnimeSpider(AmazonAnimeSpider amazonAnimeSpider) {
        this.amazonAnimeSpider = amazonAnimeSpider;
    }

    @Autowired
    public void setAmazonRankSpider(AmazonRankSpider amazonRankSpider) {
        this.amazonRankSpider = amazonRankSpider;
    }

    @Autowired
    public void setEveryHourCompute(EveryHourCompute everyHourCompute) {
        this.everyHourCompute = everyHourCompute;
    }

}
