package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.disc.Disc;
import com.animediscs.model.disc.DiscAmazon;
import com.animediscs.runner.SpiderService;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonSpeedSpider {

    private Pattern pattern = Pattern.compile("dp/([A-Z0-9]{10})/ref=zg_bs_dvd_(\\d+)");

    private Logger logger = LogManager.getLogger(AmazonSpeedSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doUpdate(SpiderService service, int level) {
        for (int page = 1; page <= 5; page++) {
            int start = page * 20 - 19;
            logger.printf(Level.INFO, "开始更新Amazon速报数据(%02d - %02d)", start, start + 19);
            String format = "http://www.amazon.co.jp/gp/bestsellers/dvd/ref=zg_bs_dvd_pg_%1$d?ie=UTF8&pg=%1$d";
            service.addTask(level, String.format(format, page), document -> {
                document.select("div.zg_title a").forEach(link -> {
                    Matcher matcher = pattern.matcher(link.attr("href"));
                    if (matcher.find()) {
                        updateDiscAmazon(matcher.group(1), matcher.group(2));
                    } else {
                        logger.printf(Level.DEBUG, "未找到Amazon速报数据, 跳过此链接: %s", link.attr("href"));
                    }
                });
                logger.printf(Level.INFO, "成功更新Amazon速报数据(%02d - %02d)", start, start + 19);
            });
        }
    }

    private void updateDiscAmazon(String asin, String rank) {
        Disc disc = dao.lookup(Disc.class, "asin", asin);
        if (disc != null) {
            DiscAmazon discAmazon = getDiscAmazon(disc);
            discAmazon.setSpdt(new Date());
            discAmazon.setSprk(Integer.parseInt(rank));
            dao.saveOrUpdate(discAmazon);
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
