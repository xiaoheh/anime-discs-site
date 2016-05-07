package fands.spdier;

import fands.dao.Dao;
import fands.model.DiscList;
import fands.model.Season;
import fands.model.disc.Disc;
import fands.model.disc.DiscSakura;
import fands.support.Constants;
import fands.support.HelpUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static fands.support.Constants.TOP_100_NAME;
import static fands.support.HelpUtil.parseType;

@Service
public class SakuraSpeedSpider {

    private SimpleDateFormat updateTimeFormat = new SimpleDateFormat("yyyy年M月d日 H時m分s秒");

    private Logger logger = LogManager.getLogger(SakuraSpeedSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void parseDocument(Document document) {
        Elements tables = document.select("table");
        Elements fonts = document.select("b>font[color=red]");
        for (int i = 0; i < tables.size(); i++) {
            updateDiscList(tables.get(i), fonts.get(i).text());
        }
    }

    private void updateDiscList(Element table, String updateText) {
        DiscList discList = getDiscList(table.parent().id());
        if (!updateText.equals("更新中")) {
            Date updateTime = parseUpdateTime(updateText);
            if (discList.getDate() == null || discList.getDate().compareTo(updateTime) < 0) {
                discList.setDate(updateTime);
                discList.setDiscs(new LinkedList<>());
                boolean top100 = TOP_100_NAME.equals(discList.getName());

                table.select("tr").stream().skip(1).forEach(tr -> {
                    String asin = tr.child(5).child(0).attr("href").substring(11);
                    String name = nameOfDisc(tr.child(5).text());
                    String type = tr.child(1).text();
                    Disc disc = getDisc(asin, name, type);
                    if (disc.getSeason() == null && !top100) {
                        Season season = getOrCreateSeason(discList.getName());
                        disc.setSeason(season);
                    }
                    dao.saveOrUpdate(disc);

                    DiscSakura discSakura = getDiscSakura(disc);
                    String[] sakuraRank = tr.child(0).text().split("/");
                    discSakura.setCurk(HelpUtil.parseNumber(sakuraRank[0]));
                    discSakura.setPrrk(HelpUtil.parseNumber(sakuraRank[1]));
                    discSakura.setSpdt(updateTime);
                    dao.saveOrUpdate(discSakura);

                    discList.getDiscs().add(disc);
                });

                dao.saveOrUpdate(discList);

                logger.printf(Level.INFO, "成功更新Sakura速报数据(%s)", discList.getTitle());
            } else {
                logger.printf(Level.INFO, "不需更新Sakura速报数据(%s)", discList.getTitle());
            }
        } else {
            String name = table.parent().id();
            Assert.notNull(name);
            logger.printf(Level.INFO, "延后更新Sakura速报数据(%s), 原因: Sakura网站数据更新中.", discList.getTitle());
        }
    }

    private DiscList getDiscList(String name) {
        if (name == null || name.isEmpty()) {
            DiscList discList = dao.lookup(DiscList.class, "name", Constants.TOP_100_NAME);
            return discList == null ? Constants.TOP_100 : discList;
        }
        DiscList discList = dao.lookup(DiscList.class, "name", name);
        if (discList == null) {
            discList = new DiscList();
            discList.setName(name);
            discList.setTitle(name.substring(0, 4) + "年" + name.substring(5) + "月新番");
        }
        return discList;
    }

    private Disc getDisc(String asin, String name, String type) {
        Disc disc = dao.lookup(Disc.class, "asin", asin);
        if (disc == null) {
            disc = new Disc();
            disc.setAsin(asin);
            disc.setJapan(name);
            disc.setAmzver(name.startsWith("【Amazon.co.jp限定】"));
            disc.setTitle(titleOfDisc(name));
            disc.setType(parseType(type));
        }
        return disc;
    }

    private DiscSakura getDiscSakura(Disc disc) {
        DiscSakura discSakura = disc.getSakura();
        if (discSakura == null) {
            discSakura = new DiscSakura();
            discSakura.setDisc(disc);
        }
        return discSakura;
    }

    private Season getOrCreateSeason(String name) {
        Season season = dao.lookup(Season.class, "name", name);
        if (season == null) {
            season = new Season();
            season.setName(name);
            dao.save(season);
        }
        return season;
    }

    private Date parseUpdateTime(String dateText) {
        try {
            Date date = updateTimeFormat.parse(dateText);
            return DateUtils.addHours(date, -1);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String nameOfDisc(String discText) {
        discText = discText.replace("【予約不可】", "");
        discText = discText.replace("【更新停止】", "");
        return discText;
    }

    private String titleOfDisc(String discName) {
        discName = discName.replace("【Blu-ray】", " [Blu-ray]");
        discName = discName.replace("【DVD】", " [DVD]");
        if (discName.startsWith("【Amazon.co.jp限定】")) {
            discName = discName.substring(16).trim() + "【尼限定】";
        }
        discName = discName.replaceAll("\\s+", " ");
        return discName;
    }

}
