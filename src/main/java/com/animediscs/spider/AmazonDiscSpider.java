package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.runner.task.DiscSpiderTask;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.animediscs.util.Parser.*;

@Service
public class AmazonDiscSpider {

    private Logger logger = LogManager.getLogger(AmazonDiscSpider.class);
    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void doCreateDisc(SpiderService service, int level, Long id, String asin) {
        logger.printf(Level.INFO, "准备抓取碟片信息: %s", asin);
        service.addTask(level, new DiscSpiderTask(asin, () -> true, document -> {
            addDiscFromAmazon(asin, id, document);
            logger.printf(Level.INFO, "成功抓取碟片信息: %s", asin);
        }));
    }

    private void addDiscFromAmazon(String asin, Long id, Document document) {
        Node itemAttributes = getNode(document, "ItemAttributes");
        if (itemAttributes == null) {
            throw new RuntimeException("未返回碟片详细信息, ASIN=" + asin);
        }
        Document items = itemAttributes.getOwnerDocument();
        Disc disc = new Disc();
        disc.setAsin(asin);
        disc.setJapan(getValue(items, "Title"));
        disc.setTitle(Disc.titleOfDisc(disc.getJapan()));
        disc.setAmzver(Disc.isAmzver(disc.getJapan()));
        setType(disc, getValue(items, "ProductGroup"));
        setRelease(disc, getValue(items, "ReleaseDate"));
        dao.save(disc);
        Node rank = getNode(document, "SalesRank");
        if (rank != null) {
            updateRank(getDiscRank(disc), getValue(rank));
        }
        dao.execute(session -> {
            dao.get(DiscList.class, id).getDiscs().add(disc);
        });
    }

    private void setRelease(Disc disc, String dateText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        disc.setRelease(parseDate(sdf, dateText));
    }

    private void setType(Disc disc, String typeText) {
        switch (typeText) {
            case "Music":
                disc.setType(DiscType.CD);
                break;
            case "DVD":
                if (disc.getJapan().contains("Blu-ray")) {
                    disc.setType(DiscType.BD);
                } else {
                    disc.setType(DiscType.DVD);
                }
                break;
            default:
                disc.setType(DiscType.OTHER);
                break;
        }
    }

    private DiscRank getDiscRank(Disc disc) {
        DiscRank rank = disc.getRank();
        if (rank == null) {
            rank = new DiscRank();
            rank.setDisc(disc);
        } else {
            dao.refresh(rank);
        }
        return rank;
    }

    private void updateRank(DiscRank rank, String rankText) {
        rank.setPark(parseNumber(rankText));
        rank.setPadt(new Date());
        if (rank.getPark() != rank.getPark1()) {
            pushRank(rank);
            saveRank(rank);
        }
        dao.saveOrUpdate(rank);
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

    private Node getNode(Document document, String itemAttributes) {
        return document.getElementsByTagName(itemAttributes).item(0);
    }

    private String getValue(Document items, String title) {
        return getValue(getNode(items, title));
    }

    private String getValue(Node rank) {
        return rank.getTextContent();
    }

}
