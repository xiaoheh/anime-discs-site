package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.Disc;
import com.animediscs.runner.SpiderService;
import com.animediscs.util.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.animediscs.util.Helper.readAllLines;

@Service
public class AmazonAnimeSpider {

    private Logger logger = LogManager.getLogger(AmazonAnimeSpider.class);
    private Pattern pattern = Pattern.compile("\\d{4}年\\d+月\\d+日");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdate(SpiderService service, int level) {
        readAllLines("config/disclist.txt").forEach(asin -> {
            Disc disc = dao.lookup(Disc.class, "asin", asin);
            if (disc == null) {
                logger.info("准备抓取碟片信息: " + asin);
                service.addTask(level, "http://www.amazon.co.jp/dp/" + asin, document -> {
                    addDiscFromAmazon(asin, document);
                    logger.info("成功抓取碟片信息: " + asin);
                });
            }
        });
    }

    private void addDiscFromAmazon(String asin, Document document) {
        String japan = document.select("#productTitle").text().trim();
        Elements elements = document.select("#byline span");
        String type = elements.get(elements.size() - 1).text().trim();
        Disc disc = new Disc();
        disc.setAsin(asin);
        disc.setJapan(japan);
        disc.setTitle(Disc.titleOfDisc(japan));
        disc.setDvdver(type.equals("DVD"));
        disc.setAmzver(Disc.isAmzver(japan));

        Elements elements2 = document.select("#availability>span.a-color-success");
        if (elements2.size() > 0) {
            Matcher matcher = pattern.matcher(elements2.get(0).text().trim());
            if (matcher.find()) {
                disc.setRelease(Parser.parseDate(dateFormat, matcher.group()));
            }
        }

        dao.save(disc);
    }

}
