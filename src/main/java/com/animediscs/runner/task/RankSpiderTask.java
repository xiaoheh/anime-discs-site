package com.animediscs.runner.task;

import com.animediscs.runner.SpiderTask;
import com.animediscs.spider.SignedRequestsHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RankSpiderTask implements SpiderTask {

    private static final String AWS_ACCESS_KEY_ID = "AKIAIYEBYCYVUWSSTGEA";
    private static final String AWS_SECRET_KEY = "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";
    private static final String ASSOCIATE_TAG = "animediscs-20";
    private static final String ENDPOINT = "ecs.amazonaws.jp";
    private static SignedRequestsHelper helper;

    static {
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, ASSOCIATE_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String text;
    private Supplier<Boolean> test;
    private Consumer<Document> consumer;
    private Document document;
    private int tryCount;
    private int maxRetry;

    public RankSpiderTask(String text, Supplier<Boolean> test, Consumer<Document> consumer) {
        this.text = text;
        this.test = test;
        this.consumer = consumer;
        this.maxRetry = MAX_RETRY_COUNT;
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
                params.put("ResponseGroup", "SalesRank");

                String requestUrl = helper.sign(params);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                document = db.parse(requestUrl);
                tryCount = 0;
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
