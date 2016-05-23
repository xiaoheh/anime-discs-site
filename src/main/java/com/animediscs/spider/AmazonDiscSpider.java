package com.animediscs.spider;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.runner.SpiderService;
import com.animediscs.runner.task.DiscSpiderTask;
import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

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
        Node itemAttributes = document.getElementsByTagName("ItemAttributes").item(0);
        NodeList childNodes = itemAttributes.getChildNodes();
        Disc disc = new Disc();
        disc.setAsin(asin);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals("Title")) {
                disc.setJapan(node.getTextContent());
                disc.setTitle(Disc.titleOfDisc(disc.getJapan()));
                disc.setAmzver(Disc.isAmzver(disc.getJapan()));
            } else if (node.getNodeName().equals("ProductGroup")) {
                switch (node.getTextContent()) {
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
            } else if (node.getNodeName().equals("ReleaseDate")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                disc.setRelease(parseDate(sdf, node.getTextContent()));
            }

        }
        dao.save(disc);

        Node rank = document.getElementsByTagName("SalesRank").item(0);
        updateRank(getDiscRank(disc), rank.getTextContent());

        dao.execute(session -> {
            dao.get(DiscList.class, id).getDiscs().add(disc);
        });
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

}
