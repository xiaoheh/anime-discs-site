package fands.spdier;

import fands.dao.Dao;
import fands.model.disc.Disc;
import fands.model.disc.DiscSakura;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fands.support.HelpUtil.parseNumber;

@Service
public class SakuraDiscSpider {

    private Pattern sakuraRank = Pattern.compile("【(\\d{4}年 \\d{2}月 \\d{2}日 \\d{2}時)\\(.\\)】 ([*,0-9]{7})位");
    private Pattern sakuraPont = Pattern.compile("【(\\d{4}年 \\d{2}月 \\d{2}日)\\(.\\)】 ([*,0-9]{7})");
    private Pattern sakuraCapt = Pattern.compile(":(\\d+)pt\\(残り日数(\\d+)\\)");
    private Pattern sakuraCupt = Pattern.compile(":(\\d+)pt");
    private Pattern sakuraBook = Pattern.compile("(\\d+)\\(前日(\\d+)\\)");

    private SimpleDateFormat rankDateFormat = new SimpleDateFormat("yyyy年 MM月 dd日 HH時");
    private SimpleDateFormat pontDateFormat = new SimpleDateFormat("yyyy年 MM月 dd日");
    private SimpleDateFormat discDateFormat = new SimpleDateFormat("yyyy年MM月dd日");


    private Logger logger = LogManager.getLogger(SakuraDiscSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void parseDocument(Disc disc, Document document) {
        dao.refresh(disc);

        String releaseText = document.select("#newdatatab font[color=navy]").text();
        String shelvesText = document.select("#newdatatab font[color=red]").get(0).text();
        disc.setRelease(parseDiscDate(releaseText));
        disc.setShelves(parseDiscDate(shelvesText));

        dao.update(disc);

        DiscSakura discSakura = getDiscSakura(disc);
        discSakura.setPadt(new Date());

        if (needUpdateRank(discSakura.getSpdt())) {
            String rankText = document.select("#rankdatatab textarea").text();
            Matcher rankMatcher = sakuraRank.matcher(rankText);
            discSakura.setSpdt(new Date());
            while (rankMatcher.find()) {
                discSakura.setPrrk(discSakura.getCurk());
                discSakura.setCurk(parseNumber(rankMatcher.group(2)));
            }
        }

        Elements select = document.select("#newdatatab font[color=blue]");
        String cubkText = select.get(select.size() - 1).text();
        Matcher bookMatcher = sakuraBook.matcher(cubkText);
        if (bookMatcher.find()) {
            discSakura.setCubk(Integer.parseInt(bookMatcher.group(1)));
            discSakura.setPrbk(Integer.parseInt(bookMatcher.group(2)));
        } else {
            discSakura.setCubk(-1);
            discSakura.setPrbk(-1);
        }

        String cuptText = document.select("#newdatatab font[color=red]").get(1).text();
        Matcher cuptMatcher = sakuraCupt.matcher(cuptText);
        if (cuptMatcher.find()) {
            discSakura.setCupt(Integer.parseInt(cuptMatcher.group(1)));
        } else {
            discSakura.setCupt(-1);
        }

        String prptText = document.select("#ptdatatab textarea").text();
        Matcher ptptMatcher = sakuraPont.matcher(prptText);
        Date prevRelease = DateUtils.addDays(disc.getRelease(), -1);
        Date prevToday = DateUtils.addHours(new Date(), -24);
        discSakura.setPrpt(-1);
        while (ptptMatcher.find()) {
            Date date = parsePontDate(ptptMatcher.group(1));
            if (DateUtils.isSameDay(date, prevRelease) || DateUtils.isSameDay(date, prevToday)) {
                discSakura.setPrpt(parseNumber(ptptMatcher.group(2)));
            }
        }

        String captText = document.select("#newdatatab font[color=red]").get(2).text();
        Matcher captMatcher = sakuraCapt.matcher(captText);
        if (captMatcher.find()) {
            discSakura.setCapt(Integer.parseInt(captMatcher.group(1)));
            discSakura.setSday(Integer.parseInt(captMatcher.group(2)));
        } else {
            discSakura.setCapt(-1);
            discSakura.setSday(getSday(disc.getRelease()));
        }

        dao.saveOrUpdate(discSakura);
    }

    private boolean needUpdateRank(Date date) {
        Date needUpdate = DateUtils.addHours(new Date(), -4);
        return date == null || date.compareTo(needUpdate) < 0;
    }

    private int getSday(Date date) {
        long millis = date.getTime() - System.currentTimeMillis();
        return (int) (millis / 1000 / 3600 / 24);
    }

    private DiscSakura getDiscSakura(Disc disc) {
        DiscSakura discSakura = disc.getSakura();
        if (discSakura == null) {
            discSakura = new DiscSakura();
            discSakura.setDisc(disc);
        }
        return discSakura;
    }

    private Date parseRankDate(String dateText) {
        try {
            return rankDateFormat.parse(dateText);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Date parseDiscDate(String dateText) {
        try {
            return discDateFormat.parse(dateText);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Date parsePontDate(String dateText) {
        try {
            return pontDateFormat.parse(dateText);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
