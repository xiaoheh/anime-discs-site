package fands.spdier;

import fands.dao.Dao;
import fands.model.disc.Disc;
import fands.model.disc.DiscAmazon;
import org.apache.logging.log4j.*;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonSpeedSpider {

    private Pattern amazonSpeed = Pattern.compile("dp/([A-Z0-9]{10})/ref=zg_bs_dvd_(\\d+)");

    private Logger logger = LogManager.getLogger(AmazonSpeedSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void parseDoucment(Document document) {
        document.select("div.zg_title a").forEach(link -> {
            Matcher matcher = amazonSpeed.matcher(link.attr("href"));
            if (matcher.find()) {
                Disc disc = dao.lookup(Disc.class, "asin", matcher.group(1));
                if (disc != null) {
                    DiscAmazon discAmazon = getDiscAmazon(disc);
                    discAmazon.setSpdt(new Date());
                    discAmazon.setSprk(Integer.parseInt(matcher.group(2)));
                    dao.saveOrUpdate(discAmazon);
                }
            } else {
                logger.printf(Level.DEBUG, "未找到Amazon速报数据, 跳过此链接: %s", link.attr("href"));
            }
        });
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
