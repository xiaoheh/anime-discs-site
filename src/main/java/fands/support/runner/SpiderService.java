package fands.support.runner;

import fands.support.HelpUtil;
import org.apache.logging.log4j.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SpiderService {

    private static final int SAKURA_MAX_CONNECT_THREAD = 2;
    private static final int AMAZON_MAX_CONNECT_THREAD = 2;

    private Logger logger = LogManager.getLogger(SpiderService.class);

    private ExecutorService sakuraConnnect = Executors.newFixedThreadPool(SAKURA_MAX_CONNECT_THREAD);
    private ExecutorService amazonConnnect = Executors.newFixedThreadPool(AMAZON_MAX_CONNECT_THREAD);
    private ExecutorService execute = Executors.newFixedThreadPool(1);

    private List<SpiderTask> sakura1 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> sakura2 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> sakura3 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> sakura4 = Collections.synchronizedList(new LinkedList<>());

    private List<SpiderTask> amazon1 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> amazon2 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> amazon3 = Collections.synchronizedList(new LinkedList<>());
    private List<SpiderTask> amazon4 = Collections.synchronizedList(new LinkedList<>());

    private AtomicInteger sakuraRunning = new AtomicInteger(0);
    private AtomicInteger amazonRunning = new AtomicInteger(0);

    private long startupTimestamp = System.currentTimeMillis();
    private final Object waitObject = new Object();

    @PostConstruct
    public void initSpiderService() {
        logger.info("爬虫服务已启动...");

        /**
         * 任务监控线程
         */
        newTimerSchedule(0, 30, () -> {
            String timeout = HelpUtil.formatTimeout(startupTimestamp);
            logger.printf(Level.INFO, "(%s): schedule sakura task1: %d, task2: %d, task3: %d, task4: %d",
                    timeout, sakura1.size(), sakura2.size(), sakura3.size(), sakura4.size());
            logger.printf(Level.INFO, "(%s): schedule amazon task1: %d, task2: %d, task3: %d, task4: %d",
                    timeout, amazon1.size(), amazon2.size(), amazon3.size(), amazon4.size());
        });

        /**
         * 任务调度线程
         */
        new Thread(() -> {
            while (true) {
                synchronized (waitObject) {
                    if (amazonRunning.get() < AMAZON_MAX_CONNECT_THREAD + 1) {
                        if (trySubmitTask(amazonRunning, amazonConnnect, amazon1)) continue;
                        if (trySubmitTask(amazonRunning, amazonConnnect, amazon2)) continue;
                        if (trySubmitTask(amazonRunning, amazonConnnect, amazon3)) continue;
                        if (trySubmitTask(amazonRunning, amazonConnnect, amazon4)) continue;
                    }
                    if (sakuraRunning.get() < SAKURA_MAX_CONNECT_THREAD + 1) {
                        if (trySubmitTask(sakuraRunning, sakuraConnnect, sakura1)) continue;
                        if (trySubmitTask(sakuraRunning, sakuraConnnect, sakura2)) continue;
                        if (trySubmitTask(sakuraRunning, sakuraConnnect, sakura3)) continue;
                        if (trySubmitTask(sakuraRunning, sakuraConnnect, sakura4)) continue;
                    }
                    try {
                        waitObject.wait(1000);
                    } catch (InterruptedException e) {
                        logger.warn("调度线程收到意外的中断信号, 已忽略该信号.");
                    }
                }
            }
        }).start();
    }

    public List<SpiderTask> getSakura1() {
        return sakura1;
    }

    public List<SpiderTask> getSakura2() {
        return sakura2;
    }

    public List<SpiderTask> getSakura3() {
        return sakura3;
    }

    public List<SpiderTask> getSakura4() {
        return sakura4;
    }

    public List<SpiderTask> getAmazon1() {
        return amazon1;
    }

    public List<SpiderTask> getAmazon2() {
        return amazon2;
    }

    public List<SpiderTask> getAmazon3() {
        return amazon3;
    }

    public List<SpiderTask> getAmazon4() {
        return amazon4;
    }

    public void nodifyWaitObject() {
        synchronized (waitObject) {
            waitObject.notify();
        }
    }

    private void newTimerSchedule(long delay, long period, final Runnable runnable) {
        new Timer(true).schedule(new TimerTask() {
            public void run() {
                runnable.run();
            }
        }, delay * 1000, period * 1000);
    }

    private boolean trySubmitTask(AtomicInteger taskCount, ExecutorService connect, List<SpiderTask> taskList) {
        if (taskList.size() > 0) {
            taskCount.incrementAndGet();
            SpiderTask task = taskList.remove(0);
            connect.execute(() -> {
                try {
                    task.doConnect();
                    addExecuteTask(task);
                } catch (IOException e) {
                    tryReConnect(taskList, task, e);
                } catch (Exception e) {
                    logger.printf(Level.WARN, "connect service throws exception: %s %s", e.getClass(), e.getMessage());
                    logger.debug("connect service throws exception:", e);
                } finally {
                    synchronized (waitObject) {
                        taskCount.decrementAndGet();
                        waitObject.notify();
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void tryReConnect(List<SpiderTask> taskList, SpiderTask task, IOException e) {
        if (e == null) {
            logger.printf(Level.DEBUG, "暂时没有代理资源, 已安排重试, URL=%s", task.getUrl());
            taskList.add(task);
        } else if (task.isContinue(e)) {
            logger.printf(Level.DEBUG, "Connect任务失败, 已安排重试, URL=%s", task.getUrl());
            taskList.add(task);
        } else {
            logger.printf(Level.INFO, "Connect任务在尝试%d次后依然失败", SpiderTask.MAX_RETRY_COUNT);
            logger.printf(Level.INFO, "Connect任务的链接为: %s", task.getUrl());
            logger.printf(Level.INFO, "最后的错误信息为: %s %s", e.getClass(), e.getMessage());
            logger.printf(Level.DEBUG, "详细的错误信息为:", e);
        }
    }

    private void addExecuteTask(SpiderTask task) {
        execute.execute(() -> {
            try {
                task.doExecute();
            } catch (DataAccessException e) {
                tryReExecute(task, e);
            } catch (Exception e) {
                logger.printf(Level.WARN, "execute service throws exception: %s %s", e.getClass(), e.getMessage());
                logger.debug("execute service throws exception:", e);
            }
        });
    }

    private void tryReExecute(SpiderTask task, DataAccessException e) {
        if (task.isContinue(e)) {
            logger.printf(Level.DEBUG, "Execute任务失败, 已安排重试, URL=%s", task.getUrl());
            addExecuteTask(task);
        } else {
            logger.printf(Level.INFO, "Execute任务在尝试%d次后依然失败", SpiderTask.MAX_RETRY_COUNT);
            logger.printf(Level.INFO, "Execute任务的链接为: %s", task.getUrl());
            logger.printf(Level.INFO, "最后的错误信息为: %s %s", e.getClass(), e.getMessage());
            logger.printf(Level.DEBUG, "详细的错误信息为:", e);

        }
    }

}
