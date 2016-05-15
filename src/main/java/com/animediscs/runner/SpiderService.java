package com.animediscs.runner;

import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.animediscs.util.Format.formatError;
import static com.animediscs.util.Helper.*;

public class SpiderService {

    private Logger logger = LogManager.getLogger(SpiderService.class);

    private final String name;
    private final int maxLevel;
    private final int maxThread;
    private final ExecutorService connect;
    private final ExecutorService execute;
    private final AtomicInteger runningTask;
    private final List<SpiderTask>[] tasksArray;

    public SpiderService(String name, int maxLevel, int maxThread, ExecutorService execute) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.maxThread = maxThread;
        this.execute = execute;
        this.connect = Executors.newFixedThreadPool(maxThread);
        this.runningTask = new AtomicInteger(0);
        this.tasksArray = new List[maxLevel];
        startSubmitThread();
    }

    private void startSubmitThread() {
        Thread thread = new Thread(() -> {
            SUBMIT_LOOP:
            while (true) {
                if (runningTask.get() < this.maxThread) {
                    for (List<SpiderTask> taskList : tasksArray) {
                        if (taskList != null && taskList.size() > 0) {
                            SpiderTask task = taskList.remove(0);
                            runningTask.incrementAndGet();
                            doSubmitForConnect(taskList, task);
                            continue SUBMIT_LOOP;
                        }
                    }
                    waitFor(tasksArray);
                } else {
                    waitFor(runningTask);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void addTask(int level, String url, Supplier<Boolean> test, Consumer<Document> consumer) {
        addTask(level, new SpiderTask(url, test, consumer));
    }

    public void addTask(int level, String url, Consumer<Document> consumer) {
        addTask(level, new SpiderTask(url, () -> true, consumer));
    }

    private void addTask(int level, SpiderTask task) {
        Assert.isTrue(level >= 1 && level <= maxLevel);
        int index = level - 1;
        if (tasksArray[index] == null) {
            synchronized (tasksArray) {
                if (tasksArray[index] == null) {
                    tasksArray[index] = Collections.synchronizedList(new LinkedList<>());
                }
            }
        }
        tasksArray[index].add(task);
        nodify(tasksArray);
    }

    public String getStatus() {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append("抓取线程状态报告");
        builder.append(", 运行中: ").append(runningTask.get());
        builder.append(", 计划中: [");
        boolean first = true;
        for (int i = 0; i < tasksArray.length; i++) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            if (tasksArray[i] != null) {
                builder.append(tasksArray[i].size());
            } else {
                builder.append(0);
            }
        }
        builder.append("]");
        return builder.toString();
    }

    private void doSubmitForConnect(List<SpiderTask> taskList, SpiderTask task) {
        connect.execute(() -> {
            try {
                task.doConnect();
                doSumitForUpdate(task);
            } catch (IOException e) {
                if (task.isContinue(e)) {
                    taskList.add(task);
                    String format = "%s 抓取任务遇到网络错误, 已安排重试, 任务链接为: %s";
                    logger.printf(Level.INFO, format, name, task.getUrl());
                    logger.catching(Level.DEBUG, e);
                } else {
                    String format = "%s 抓取任务在尝试%d次后依然失败, 错误信息为: %s, 任务链接为: %s";
                    logger.printf(Level.INFO, format, name, task.getTryCount(), formatError(e), task.getUrl());
                    logger.catching(Level.DEBUG, e);
                }
            } catch (Exception e) {
                String format = "%s 抓取任务线程遇到意外错误: %s";
                logger.printf(Level.WARN, format, name, formatError(e));
                logger.catching(Level.DEBUG, e);
            } finally {
                runningTask.decrementAndGet();
                nodify(runningTask);
            }
        });
    }

    private void doSumitForUpdate(SpiderTask task) {
        execute.execute(() -> {
            try {
                task.doExecute();
            } catch (DataAccessException e) {
                if (task.isContinue(e)) {
                    doSumitForUpdate(task);
                    String format = "%s 更新任务遇到数据库错误, 已安排重试, 任务链接为: %s";
                    logger.printf(Level.DEBUG, format, name, task.getUrl());
                    logger.catching(Level.DEBUG, e);
                } else {
                    String format = "%s 更新任务在尝试%d次后依然失败, 错误信息为: %s, 任务链接为: %s";
                    logger.printf(Level.INFO, format, name, task.getTryCount(), formatError(e), task.getUrl());
                    logger.catching(Level.DEBUG, e);

                }
            } catch (Exception e) {
                String format = "%s 更新任务线程遇到意外错误: %s";
                logger.printf(Level.WARN, format, name, formatError(e));
                logger.catching(Level.DEBUG, e);
            }
        });
    }

}
