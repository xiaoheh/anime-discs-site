package com.animediscs.runner;

import com.animediscs.spider.*;
import com.animediscs.util.Format;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AutoRunner {

    private Logger logger = LogManager.getLogger(AutoRunner.class);
    private long startupTimeMillis = System.currentTimeMillis();

    private SpiderService sakuraRunner;
    private SpiderService amazonRunner;

    private SakuraSpeedSpider sakuraSpeedSpider;
    private AmazonSpeedSpider amazonSpeedSpider;
    private AmazonAnimeSpider amazonAnimeSpider;
    private AmazonDiscSpider amazonDiscSpider;

    public AutoRunner() {
        ExecutorService execute = Executors.newFixedThreadPool(1);
        sakuraRunner = new SpiderService("Sakura", 1, 2, execute);
        amazonRunner = new SpiderService("Amazon", 5, 2, execute);
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
    public void setAmazonDiscSpider(AmazonDiscSpider amazonDiscSpider) {
        this.amazonDiscSpider = amazonDiscSpider;
    }

    @PostConstruct
    public void startAutoRunner() {
        schedule("Sakura速报数据抓取", 5, 60, () -> {
            sakuraSpeedSpider.doUpdate(sakuraRunner, 1);
        });
        schedule("Amazon速报数据抓取", 10, 60, () -> {
            amazonSpeedSpider.doUpdate(amazonRunner, 1);
        });
        schedule("Amazon重点排名抓取", 15, 60, () -> {
            amazonDiscSpider.doUpdateHot(30, amazonRunner, 2);
        });
        schedule("Amazon次要排名抓取", 20, 240, () -> {
            amazonDiscSpider.doUpdateExt(120, amazonRunner, 3);
        });
        schedule("Amazon全部排名抓取", 25, 1800, () -> {
            amazonDiscSpider.doUpdateAll(900, amazonRunner, 4);
        });
        schedule("Amazon动画数据抓取", 35, 3600, () -> {
            amazonAnimeSpider.doUpdate(amazonRunner, 5);
        });
        schedule("任务线程状态报告", 0, 30, () -> {
            String timeout = Format.formatTimeout(startupTimeMillis);
            logger.printf(Level.INFO, "(%s): %s", timeout, sakuraRunner.getStatus());
            logger.printf(Level.INFO, "(%s): %s", timeout, amazonRunner.getStatus());
        });
    }

    private void schedule(String name, long delay, long period, Callable callable) {
        logger.printf(Level.INFO, "已添加计划任务 %s, %d秒后开始运行, 每%d秒运行一次", name, delay, period);
        new Timer(true).schedule(new TimerTask() {
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    logger.printf(Level.WARN, "计划任务 %s 出现错误: %s: %s",
                            name, e.getClass().getSimpleName(), e.getMessage());
                    logger.catching(Level.DEBUG, e);
                }
            }
        }, delay * 1000, period * 1000);
    }

    private interface Callable {
        void call() throws Exception;
    }

}
