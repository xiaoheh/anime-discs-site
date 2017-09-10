package com.animediscs.runner.task;

import com.animediscs.runner.SpiderTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JsoupSpiderTask implements SpiderTask {

    private String text;
    private Supplier<Boolean> test;
    private Consumer<Document> consumer;
    private Document document;
    private int tryCount;
    private int maxRetry;

    public JsoupSpiderTask(String text, Supplier<Boolean> test, Consumer<Document> consumer) {
        this.text = text;
        this.test = test;
        this.consumer = consumer;
        this.maxRetry = MAX_RETRY_COUNT;
    }

    @Override
    public void doConnect() throws IOException {
        if (test.get()) {
            document = Jsoup.connect(text)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get();
            tryCount = 0;
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
