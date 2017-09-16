package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.runner.task.JsoupSpiderTask;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static com.animediscs.model.Disc.*;
import static com.animediscs.model.DiscList.titleOfSeason;
import static com.animediscs.util.Helper.getSday;
import static com.animediscs.util.Parser.*;

@Service
public class SakuraSpeedSpider {

    private static final String SAKURA_SPEED_URL = "http://rankstker.net/index_news.cgi";

    private SimpleDateFormat update = new SimpleDateFormat("yyyy年M月d日 H時m分s秒");
    private SimpleDateFormat release = new SimpleDateFormat("yyyy/MM/dd");

    private Logger logger = LogManager.getLogger(SakuraSpeedSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdate(SpiderService service, int level) {
        logger.printf(Level.INFO, "开始更新Sakura速报数据");
        Consumer<Document> consumer = document -> {
            Elements tables = document.select("table");
            Elements fonts = document.select("b>font[color=red]");
            for (int i = 0; i < tables.size(); i++) {
                updateDiscList(tables.get(i), fonts.get(i).text());
            }
            logger.printf(Level.INFO, "成功更新Sakura速报数据");
        };
        service.addTask(level, new JsoupSpiderTask(SAKURA_SPEED_URL, () -> true, consumer));
    }

    private void updateDiscList(Element table, String updateText) {
        DiscList discList = getDiscList(table.parent().id());
        if (updateText.equals("更新中")) {
            logger.printf(Level.INFO, "延后更新Sakura速报数据(%s), 原因: Sakura网站数据更新中.", discList.getTitle());
        } else {
            Date japanDate = parseDate(update, updateText);
            Date chinaDate = DateUtils.addHours(japanDate, -1);
            if (DateUtils.addHours(chinaDate, 4).compareTo(new Date()) < 0) {
                logger.printf(Level.INFO, "暂停更新Sakura速报数据(%s), 原因: Sakura网站数据过于陈旧.", discList.getTitle());
            } else {
                updateDiscList(table, discList, chinaDate);
                logger.printf(Level.INFO, "成功更新Sakura速报数据(%s)", discList.getTitle());
            }
        }
    }

    private void updateDiscList(Element table, DiscList discList, Date updateTime) {
        LinkedList<Disc> discs = new LinkedList<>();
        table.select("tr").stream().skip(1).forEach(tr -> {
            String href = tr.child(5).child(0).attr("href");
            String asin = href.substring(href.length() - 10);
            Disc disc = getDisc(asin, tr);

            DiscSakura discSakura = getDiscSakura(disc);
            String[] sakuraRank = tr.child(0).text().split("/");
            discSakura.setCurk(parseNumber(sakuraRank[0]));
            discSakura.setPrrk(parseNumber(sakuraRank[1]));
            discSakura.setCupt(parseNumber(tr.child(2).text()));
            discSakura.setCubk(parseNumber(tr.child(3).text()));
            discSakura.setSday(getSday(disc));
            discSakura.setDate(updateTime);
            dao.saveOrUpdate(discSakura);
            discs.add(disc);
        });
        discList.setDate(updateTime);
        if (discList.isTop100()) {
            discList.setDiscs(discs);
            dao.saveOrUpdate(discList);
        } else {
            dao.execute(session -> {
                session.update(discList);
                Set<Disc> discSet = new HashSet<>(discList.getDiscs());
                discs.stream().filter(disc -> !discSet.contains(disc))
                        .forEach(discList.getDiscs()::add);
            });
        }
    }

    private Disc getDisc(String asin, Element tr) {
        Disc disc = dao.lookup(Disc.class, "asin", asin);
        if (disc == null) {
            String japan = nameOfDisc(tr.child(5).text());
            String type = tr.child(1).text();

            disc = new Disc();
            disc.setAsin(asin);
            disc.setJapan(japan);
            disc.setTitle(titleOfDisc(japan));
            disc.setRelease(parseRelease(tr));
            disc.setAmzver(isAmzver(japan));
            if (type.equals("◎")) {
                if (japan.contains("Blu-ray")) {
                    disc.setType(DiscType.BD_BOX);
                } else {
                    disc.setType(DiscType.DVD_BOX);
                }
            } else {
                if (japan.contains("Blu-ray")) {
                    disc.setType(DiscType.BD);
                } else {
                    disc.setType(DiscType.DVD);
                }
            }
        }
        dao.saveOrUpdate(disc);
        return disc;
    }

    private Date parseRelease(Element tr) {
        String dateText = tr.child(4).text();
        if (dateText.length() == 8) {
            dateText = "20" + dateText;
        }
        try {
            return parseDate(release, dateText);
        } catch (RuntimeException e) {
            return new Date();
        }
    }

    private DiscList getDiscList(String name) {
        if (name == null || name.isEmpty()) {
            return getDiscList("top_100", "日亚实时TOP100");
        } else {
            return getDiscList(name, titleOfSeason(name));
        }
    }

    private DiscList getDiscList(String name, String title) {
        DiscList discList = dao.lookup(DiscList.class, "name", name);
        if (discList == null) {
            discList = new DiscList();
            discList.setName(name);
            discList.setTitle(title);
            discList.setSakura(true);
            dao.save(discList);
        }
        return discList;
    }

    private DiscSakura getDiscSakura(Disc disc) {
        DiscSakura discSakura = disc.getSakura();
        if (discSakura == null) {
            discSakura = new DiscSakura();
            discSakura.setDisc(disc);
        }
        return discSakura;
    }

    private String nameOfDisc(String discText) {
        discText = discText.replace("【予約不可】", "");
        discText = discText.replace("【更新停止】", "");
        return discText;
    }

}
