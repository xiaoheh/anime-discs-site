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

    private Pattern rankPattern = Pattern.compile("【(\\d{4}年 \\d{2}月 \\d{2}日 \\d{2}時)\\(.\\)】 ([*,0-9]{7})位");
    private Pattern captPattern = Pattern.compile(":(\\d+)pt\\(残り日数(\\d+)\\)");
    private Pattern prptPattern = Pattern.compile("【(\\d{4}年 \\d{2}月 \\d{2}日)\\(.\\)】 ([*,0-9]{7})");
    private Pattern cuptPattern = Pattern.compile(":(\\d+)pt");
    private Pattern bookPattern = Pattern.compile("(\\d+)\\(前日(\\d+)\\)");

    private SimpleDateFormat discFormat = new SimpleDateFormat("yyyy年MM月dd日");


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
            updateRank(document, discSakura);
        }
        updateBook(document, discSakura);
        updateCupt(document, discSakura);
        updatePrpt(document, discSakura, disc.getRelease());
        updateCapt(document, discSakura, disc.getRelease());

        dao.saveOrUpdate(discSakura);
    }

    private void updateRank(Document document, DiscSakura discSakura) {
        String text = document.select("#rankdatatab textarea").text();
        String[] split = text.split("\n");
        if (split.length == 0) {
            discSakura.setPrrk(0);
            discSakura.setCurk(0);
        } else if (split.length == 1) {
            discSakura.setPrrk(0);
            discSakura.setCurk(findRank(split[0]));
        } else {
            discSakura.setPrrk(findRank(split[split.length - 2]));
            discSakura.setCurk(findRank(split[split.length - 1]));
        }
    }

    private int findRank(String input) {
        Matcher matcher = rankPattern.matcher(input);
        return matcher.find() ? parseNumber(matcher.group(2)) : 0;
    }

    private void updateBook(Document document, DiscSakura discSakura) {
        Elements elements = document.select("#newdatatab font[color=blue]");
        String text = elements.get(elements.size() - 1).text();
        Matcher matcher = bookPattern.matcher(text);
        if (matcher.find()) {
            discSakura.setCubk(Integer.parseInt(matcher.group(1)));
            discSakura.setPrbk(Integer.parseInt(matcher.group(2)));
        } else {
            discSakura.setCubk(-1);
            discSakura.setPrbk(-1);
        }
    }

    private void updateCupt(Document document, DiscSakura discSakura) {
        String text = document.select("#newdatatab font[color=red]").get(1).text();
        Matcher matcher = cuptPattern.matcher(text);
        if (matcher.find()) {
            discSakura.setCupt(Integer.parseInt(matcher.group(1)));
        } else {
            discSakura.setCupt(-1);
        }
    }

    private void updatePrpt(Document document, DiscSakura discSakura, Date release) {
        String text = document.select("#ptdatatab textarea").text();
        String[] split = text.split("\n");
        if (split.length == 0) {
            discSakura.setPrpt(-1);
        } else if (split.length == 1) {
            discSakura.setPrpt(0);
        } else {
            discSakura.setPrpt(findPrpt(split, release));
        }
    }

    private int findPrpt(String[] split, Date release) {
        Date japan = DateUtils.addHours(new Date(), 1);
        if (DateUtils.isSameDay(japan, release)) {
            return parsePrpt(split[split.length - 1]);
        } else {
            return parsePrpt(split[split.length - 2]);
        }
    }

    private int parsePrpt(String text) {
        Matcher matcher = prptPattern.matcher(text);
        if (matcher.find()) {
            return parseNumber(matcher.group(2));
        }
        return 0;
    }

    private void updateCapt(Document document, DiscSakura discSakura, Date release) {
        String text = document.select("#newdatatab font[color=red]").get(2).text();
        Matcher matcher = captPattern.matcher(text);
        if (matcher.find()) {
            discSakura.setCapt(parseNumber(matcher.group(1)));
            discSakura.setSday(parseNumber(matcher.group(2)));
        } else {
            discSakura.setCapt(-1);
            discSakura.setSday(getSday(release));
        }
    }

    private boolean needUpdateRank(Date date) {
        Date needUpdate = DateUtils.addHours(new Date(), -4);
        return date == null || date.compareTo(needUpdate) < 0;
    }

    private int getSday(Date release) {
        long currentTime = System.currentTimeMillis();
        long releaseTime = release.getTime() + 3600000L;
        return (int) ((releaseTime - currentTime) / 86400000L);
    }

    private DiscSakura getDiscSakura(Disc disc) {
        DiscSakura discSakura = disc.getSakura();
        if (discSakura == null) {
            discSakura = new DiscSakura();
            discSakura.setDisc(disc);
        }
        return discSakura;
    }

    private Date parseDiscDate(String dateText) {
        try {
            return discFormat.parse(dateText);
        } catch (ParseException e) {
            logger.warn("不能解析该日期, 错误信息为: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
