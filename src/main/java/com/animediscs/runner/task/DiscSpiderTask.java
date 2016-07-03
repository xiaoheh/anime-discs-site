package com.animediscs.runner.task;

import com.animediscs.runner.SpiderTask;
import com.animediscs.spider.SignedRequestsHelper;
import com.animediscs.util.Helper;
import org.apache.logging.log4j.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DiscSpiderTask implements SpiderTask {

    private static int count;
    private static ThreadLocal<SignedRequestsHelper> helper = ThreadLocal.withInitial(() -> {
        try {
            count++;
            Properties properties = Helper.loadProperties("config/amazon-config.txt");
            String endpoint = "ecs.amazonaws.jp";
            String accessKey = properties.getProperty("amazon.access." + count);
            String secretKey = properties.getProperty("amazon.secret." + count);
            String associateTag = properties.getProperty("amazon.userid." + count);
            return SignedRequestsHelper.getInstance(endpoint, accessKey, secretKey, associateTag);
        } catch (Exception e) {
            Logger logger = LogManager.getLogger(DiscSpiderTask.class);
            logger.printf(Level.WARN, "未能正确载入配置或初始化AmazonSpider");
            logger.catching(Level.WARN, e);
            throw new RuntimeException(e);
        }
    });

    private String text;
    private Supplier<Boolean> test;
    private Consumer<Document> consumer;
    private Document document;
    private int tryCount;
    private int maxRetry;

    public DiscSpiderTask(String text, Supplier<Boolean> test, Consumer<Document> consumer) {
        this.text = text;
        this.test = test;
        this.consumer = consumer;
        this.maxRetry = 5;
    }

    @Override
    public void doConnect() throws IOException {
        if (test.get()) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("Service", "AWSECommerceService");
                params.put("Version", "2013-08-01");
                params.put("Operation", "ItemLookup");
                params.put("ItemId", text);
                params.put("ResponseGroup", "ItemAttributes,SalesRank");

                String requestUrl = helper.get().sign(params);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                URLConnection connection = new URL(requestUrl).openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                document = db.parse(connection.getInputStream());
                tryCount = 0;
            } catch (SocketTimeoutException e) {
                throw e;
            } catch (IOException e) {
                sleepThread();
                throw e;
            } catch (ParserConfigurationException | SAXException e) {
                LogManager.getLogger(this).catching(Level.DEBUG, e);
            }
        }
    }

    private void sleepThread() {
        try {
            LogManager.getLogger(this).printf(Level.INFO, "访问Amazon API太快了, 休息10秒钟.");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doExecute() {
        consumer.accept(document);
    }

    @Override
    public boolean isContinue(Throwable t) {
        return ++tryCount < maxRetry;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getTryCount() {
        return tryCount;
    }

}
