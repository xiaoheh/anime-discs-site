package com.animediscs.spdier;

import com.animediscs.dao.Dao;
import com.animediscs.model.disc.Disc;
import com.animediscs.model.disc.DiscAmazon;
import com.animediscs.support.HelpUtil;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonDiscSpider {

    private static final Pattern rankPattern = Pattern.compile("DVD \\- ([0-9,]+)位");

    private Logger logger = LogManager.getLogger(AmazonDiscSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void parseDocument(Disc disc, Document document) {
        dao.refresh(disc);
        DiscAmazon discAmazon = getDiscAmazon(disc);
        String text = document.select("#SalesRank").text();
        Matcher matcher = rankPattern.matcher(text);
        if (matcher.find()) {
            int curk = HelpUtil.parseNumber(matcher.group(1));
            discAmazon.setPadt(new Date());
            discAmazon.setPark(curk);
        } else {
            logger.printf(Level.DEBUG, "未找到Amazon碟片数据, 跳过此碟片: %s", disc.getAsin());
        }
        dao.saveOrUpdate(discAmazon);
    }

    private DiscAmazon getDiscAmazon(Disc disc) {
        DiscAmazon discAmazon = disc.getAmazon();
        if (discAmazon == null) {
            discAmazon = new DiscAmazon();
            discAmazon.setDisc(disc);
        }
        return discAmazon;
    }

}
