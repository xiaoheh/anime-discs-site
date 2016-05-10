package fands.spdier;

import fands.dao.Dao;
import fands.model.DiscList;
import fands.model.Season;
import fands.model.disc.Disc;
import fands.model.disc.DiscSakura;
import fands.support.Constants;
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
import static fands.support.HelpUtil.parseNumber;

@Service
public class SakuraSpeedSpider {

    private SimpleDateFormat updateFormat = new SimpleDateFormat("yyyy年M月d日 H時m分s秒");
    private SimpleDateFormat releaseFormat = new SimpleDateFormat("yyyy/MM/dd");

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
            Date updateTime = DateUtils.addHours(parseDate(updateFormat, updateText), -1);
            if (needUpdate(discList, updateTime)) {
                discList.setDate(updateTime);
                discList.setDiscs(new LinkedList<>());
                boolean top100 = TOP_100_NAME.equals(discList.getName());
                Season season = top100 ? null : getOrCreateSeason(discList);

                table.select("tr").stream().skip(1).forEach(tr -> {
                    discList.getDiscs().add(updateDisc(season, tr));
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

    private boolean needUpdate(DiscList discList, Date updateTime) {
        return discList.getDate() == null || discList.getDate().compareTo(updateTime) < 0;
    }

    private Disc updateDisc(Season season, Element tr) {
        String asin = tr.child(5).child(0).attr("href").substring(11);
        String name = nameOfDisc(tr.child(5).text());
        String type = tr.child(1).text();
        String dateText = tr.child(4).text();
        if (dateText.length() == 8) {
            dateText += "20";
        }
        Date date = parseDate(releaseFormat, dateText);

        Disc disc = getDisc(asin, name, type, date, season);
        dao.saveOrUpdate(disc);

        DiscSakura discSakura = getDiscSakura(disc);
        String[] sakuraRank = tr.child(0).text().split("/");
        discSakura.setCurk(parseNumber(sakuraRank[0]));
        discSakura.setPrrk(parseNumber(sakuraRank[1]));
        discSakura.setCupt(parseNumber(tr.child(2).text()));
        discSakura.setCubk(parseNumber(tr.child(3).text()));
        discSakura.setSpdt(new Date());
        discSakura.setSday(getSday(date));
        dao.saveOrUpdate(discSakura);
        return disc;
    }

    private int getSday(Date release) {
        long currentTime = System.currentTimeMillis();
        long releaseTime = release.getTime() - 3600000L;
        return (int) ((releaseTime - currentTime) / 86400000L);
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
            discList.setTitle(titleOfList(name));
        }
        return discList;
    }

    private String titleOfList(String name) {
        return name.substring(0, 4) + "年" + name.substring(5) + "月新番";
    }

    private Disc getDisc(String asin, String name, String type, Date date, Season season) {
        Disc disc = dao.lookup(Disc.class, "asin", asin);
        if (disc == null) {
            disc = new Disc();
            disc.setAsin(asin);
            disc.setJapan(name);
            disc.setTitle(titleOfDisc(name));
            disc.setRelease(date);
            disc.setAmzver(isAmzver(name));
            if (type.equals("◎")) {
                disc.setBoxver(true);
                disc.setDvdver(!name.contains("Blu-ray"));
            } else {
                disc.setDvdver(type.equals("○"));
            }
            if (season != null) {
                disc.setSeason(season);
            }
        }
        return disc;
    }

    private boolean isAmzver(String name) {
        return name.startsWith("【Amazon.co.jp限定】");
    }

    private DiscSakura getDiscSakura(Disc disc) {
        DiscSakura discSakura = disc.getSakura();
        if (discSakura == null) {
            discSakura = new DiscSakura();
            discSakura.setDisc(disc);
        }
        return discSakura;
    }

    private Season getOrCreateSeason(DiscList discList) {
        Season season = dao.lookup(Season.class, "name", discList.getName());
        if (season == null) {
            season = new Season();
            season.setName(discList.getTitle());
            dao.save(season);
        }
        return season;
    }

    private Date parseDate(SimpleDateFormat dateFormat, String dateText) {
        try {
            return dateFormat.parse(dateText);
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
        if (isAmzver(discName)) {
            discName = discName.substring(16).trim() + "【尼限定】";
        }
        discName = discName.replaceAll("\\s+", " ");
        return discName;
    }

}
