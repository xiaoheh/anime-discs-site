package com.animediscs.runner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpiderTask {

    public static final int MAX_RETRY_COUNT = 3;

    private String url;
    private Supplier<Boolean> test;
    private Consumer<Document> consumer;
    private Document document;
    private int tryCount;
    private int maxRetry;

    public SpiderTask(String url, Supplier<Boolean> test, Consumer<Document> consumer) {
        this.url = url;
        this.test = test;
        this.consumer = consumer;
        this.maxRetry = MAX_RETRY_COUNT;
    }

    public void doConnect() throws IOException {
        if (test.get()) {
            document = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .get();
            tryCount = 0;
        }
    }

    public void doExecute() {
        consumer.accept(document);
    }

    public boolean isContinue(Throwable t) {
        return ++tryCount < maxRetry;
    }

    public String getUrl() {
        return url;
    }

    public int getTryCount() {
        return tryCount;
    }

}
