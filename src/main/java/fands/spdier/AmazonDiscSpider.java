package fands.spdier;

import fands.dao.Dao;
import fands.model.disc.Disc;
import fands.model.disc.DiscAmazon;
import fands.support.HelpUtil;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonDiscSpider {

    private Logger logger = LogManager.getLogger(AmazonDiscSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void parseDocument(Disc disc, Document document) {
        Pattern pattern = Pattern.compile("DVD \\- ([0-9,]+)位");
        Matcher matcher = pattern.matcher(document.select("#SalesRank").text());
        if (matcher.find()) {
            int curk = HelpUtil.parseNumber(matcher.group(1));
            dao.refresh(disc);
            DiscAmazon discAmazon = getDiscAmazon(disc);
            discAmazon.setPadt(new Date());
            discAmazon.setPark(curk);
            dao.saveOrUpdate(discAmazon);
        } else {
            logger.printf(Level.DEBUG, "未找到Amazon碟片数据, 跳过此碟片: %s", disc.getTitle());
        }
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
