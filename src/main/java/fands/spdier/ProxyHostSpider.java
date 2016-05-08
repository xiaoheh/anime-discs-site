package fands.spdier;

import fands.model.ProxyHost;
import fands.support.runner.ProxyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProxyHostSpider {

    private Logger logger = LogManager.getLogger(ProxyHostSpider.class);
    private ProxyService proxyService;

    @Autowired
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public void parseDocument(Document document) {
        document.select("#ip_list tr").forEach(tr -> {
            Elements tds = tr.select("td");
            if (tds.size() == 7 && tds.get(5).text().equals("HTTP")) {
                try {
                    String host = tds.get(1).text();
                    int port = Integer.parseInt(tds.get(2).text());
                    proxyService.addProxyHost(new ProxyHost(host, port));
                } catch (Exception e) {
                    logger.debug("解析代理服务器出错", e);
                }
            }
        });
        proxyService.updateProxyHost();;
    }

}
