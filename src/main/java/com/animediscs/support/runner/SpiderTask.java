package com.animediscs.support.runner;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpiderTask {

    public static final int MAX_RETRY_COUNT = 3;

    private String url;
    private Supplier<Boolean> test;
    private Consumer<Document> consumer;
    private Document document;
    private List<Throwable> errors;

    public SpiderTask(String url, Supplier<Boolean> test, Consumer<Document> consumer) {
        this.url = url;
        this.test = test;
        this.consumer = consumer;
        this.errors = new LinkedList<>();
    }

    public void doConnect() throws IOException {
        if (test.get()) {
            document = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .get();
            errors.clear();
        }
    }

    public void doExecute() {
        consumer.accept(document);
    }

    public boolean isContinue(Throwable t) {
        errors.add(t);
        return errors.size() < MAX_RETRY_COUNT;
    }

    public String getUrl() {
        return url;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != getClass()) {
            return false;
        }
        SpiderTask other = (SpiderTask) object;
        return new EqualsBuilder().append(url, other.url).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(url).toHashCode();
    }

}
