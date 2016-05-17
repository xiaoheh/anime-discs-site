package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.animediscs.util.Parser.*;
import static java.lang.System.currentTimeMillis;

@Service
public class AmazonAnimeSpider {

    private Logger logger = LogManager.getLogger(AmazonAnimeSpider.class);
    private Pattern pattern = Pattern.compile("- ([0-9,]+)位");
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdate(SpiderService service, int level) {

    }

    public void doUpdate(SpiderService service, int level, Long id, String asin) {
        logger.printf(Level.INFO, "准备抓取碟片信息: %s", asin);
        service.addTask(level, "http://www.amazon.co.jp/dp/" + asin, document -> {
            addDiscFromAmazon(asin, id, document);
            logger.printf(Level.INFO, "成功抓取碟片信息: %s", asin);
        });
    }

    private void addDiscFromAmazon(String asin, Long id, Document document) {
        String japan = document.select("#productTitle").text().trim();
        Elements elements = document.select("#byline span");
        String type = elements.get(elements.size() - 1).text().trim();
        Disc disc = new Disc();
        disc.setAsin(asin);
        disc.setJapan(japan);
        disc.setTitle(Disc.titleOfDisc(japan));
        if (type.equals("CD")) {
            disc.setCdver(true);
        } else {
            disc.setDvdver(type.equals("DVD"));
        }
        disc.setAmzver(Disc.isAmzver(japan));

        Elements elements2 = document.select("#productDetailsTable ul li");
        if (elements2.size() > 0) {
            if (disc.isCdver()) {
                Pattern pattern = Pattern.compile("(\\d{4}/\\d{1,2}/\\d{1,2})");
                elements2.forEach(element -> {
                    Matcher matcher = pattern.matcher(element.text().trim());
                    if (matcher.find()) {
                        String dateText = matcher.group();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");
                        disc.setRelease(parseDate(dateFormat, dateText));
                    }
                });
            } else {
                elements2.forEach(element -> {
                    String text = element.text().trim();
                    if (text.contains("発売日")) {
                        String dateText = text.trim().substring(text.length() - 10);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        disc.setRelease(parseDate(dateFormat, dateText));
                    }
                });
            }
        }

        dao.save(disc);

        DiscRank rank = new DiscRank();
        rank.setDisc(disc);
        updateRank(rank, document.select("#SalesRank").text());

        dao.execute(session -> {
            dao.get(DiscList.class, id).getDiscs().add(disc);
        });
    }

    private void updateRank(DiscRank rank, String rankText) {
        rank.setPadt(new Date());
        if (needUpdate(rank.getPadt1())) {
            Matcher matcher = pattern.matcher(rankText);
            if (matcher.find()) {
                rank.setPark(parseNumber(matcher.group(1)));
                if (rank.getPark() != rank.getPark1()) {
                    pushRank(rank);
                    saveRank(rank);
                }
            }
        }
        dao.saveOrUpdate(rank);
    }

    private boolean needUpdate(Date date) {
        return date == null || date.getTime() < currentTimeMillis() - 300000;
    }

    private void pushRank(DiscRank rank) {
        rank.setPadt5(rank.getPadt4());
        rank.setPadt4(rank.getPadt3());
        rank.setPadt3(rank.getPadt2());
        rank.setPadt2(rank.getPadt1());
        rank.setPadt1(rank.getPadt());
        rank.setPark5(rank.getPark4());
        rank.setPark4(rank.getPark3());
        rank.setPark3(rank.getPark2());
        rank.setPark2(rank.getPark1());
        rank.setPark1(rank.getPark());
    }

    private void saveRank(DiscRank rank) {
        DiscRecord record = new DiscRecord();
        record.setDisc(rank.getDisc());
        record.setDate(rank.getPadt());
        record.setRank(rank.getPark());
        dao.save(record);
    }

}
