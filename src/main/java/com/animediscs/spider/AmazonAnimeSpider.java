package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.runner.task.JsoupSpiderTask;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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

    public void doCreateDisc(SpiderService service, int level) {
        String url = "http://www.amazon.co.jp/b/ref=amb_link_13349006_1?ie=UTF8" +
                "&node=4367309051&pf_rd_m=AN1VRQENFRJN5&pf_rd_s=merchandised-search-leftnav" +
                "&pf_rd_r=0GGNJ6034BX1979PRQTC&pf_rd_t=101&pf_rd_p=312858289&pf_rd_i=562020";
        logger.printf(Level.INFO, "开始更新番季列表数据");
        Consumer<Document> consumer = document -> {
            Elements elements = document.select("span.h3color+a");
            elements.forEach(element -> {
                doUpdateSeason(service, level, element);
            });
            logger.printf(Level.INFO, "成功更新番季列表数据");
        };
        service.addTask(level, new JsoupSpiderTask(url, () -> true, consumer));
    }

    private void doUpdateSeason(SpiderService service, int level, Element element) {
        Season season = getSeason(element.text().trim());
        logger.printf(Level.INFO, "开始更新番季数据 %s", season.getTitle());
        String url = "http://www.amazon.co.jp" + element.attr("href");
        Consumer<Document> consumer = document -> {
            Elements elements = document.select("div.acs-feature-item a");
            doUpdateAnime(season, elements);
            logger.printf(Level.INFO, "成功更新番季数据 %s", season.getTitle());
        };
        service.addTask(level, new JsoupSpiderTask(url, () -> true, consumer));
    }

    private void doUpdateAnime(Season season, Elements elements) {
        elements.forEach(element -> {
            String japan = element.text().trim();
            Anime anime = dao.lookup(Anime.class, "japan", japan);
            if (anime == null) {
                anime = new Anime();
                anime.setJapan(japan);
                anime.setTitle(japan);
                anime.setSeason(season);
                dao.save(anime);
                logger.printf(Level.INFO, "成功添加动画数据 %s", japan);
            }
        });
    }

    private Season getSeason(String japan) {
        Season season = dao.lookup(Season.class, "japan", japan);
        if (season == null) {
            season = new Season();
            season.setJapan(japan);
            season.setTitle(titleOfSeason(japan));
            dao.save(season);
        }
        return season;
    }

    private String titleOfSeason(String japan) {
        switch (japan.substring(5, 6)) {
            case "冬":
                return japan.substring(0, 4) + "年01月新番";
            case "春":
                return japan.substring(0, 4) + "年04月新番";
            case "夏":
                return japan.substring(0, 4) + "年07月新番";
            case "秋":
                return japan.substring(0, 4) + "年10月新番";
            default:
                throw new RuntimeException("不能解析番季名称: " + japan);
        }
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
