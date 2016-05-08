package fands.support.runner;

import fands.model.ProxyHost;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.*;
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

    public void doConnect(ProxyHost proxyHost) throws IOException {
        if (test.get()) {
            if (proxyHost == null) {
                document = Jsoup.connect(url)
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .get();
            } else {
                InetSocketAddress addr = new InetSocketAddress(proxyHost.getHost(), proxyHost.getPort());
                Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
                String baseUri = "http://rankstker.net/";
                URLConnection connection = new URL(url).openConnection(proxy);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                document = Jsoup.parse(connection.getInputStream(), "Shift-JIS", baseUri);
                if (document.select("a").first().attr("href").equals("http://www27392u.sakura.ne.jp/")) {
                    proxyHost.setError(proxyHost.getError() + 100);
                    throw new RuntimeException("该代理已被封锁");
                } else {
                    proxyHost.setRight(proxyHost.getRight() + 1);
                }
            }
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
