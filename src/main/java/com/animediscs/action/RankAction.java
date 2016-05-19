package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.support.BaseAction;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Consumer;

public class RankAction extends BaseAction {

    private Dao dao;
    private Long id;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void list() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        if (disc == null) {
            responseError("没有这个碟片数据");
        } else {
            JSONObject object = new JSONObject();
            object.put("title", disc.getTitle());
            object.put("type", disc.getType().name());

            if (isQiuDvd(dao, disc)) {
                object.put("ranks", buildRanksOfPt(disc, computePtOfDvd()));
            } else if (disc.getType() == DiscType.CD) {
                object.put("ranks", buildRanksOfPt(disc, computePtOfCd()));
            } else {
                object.put("ranks", buildRanks(disc));
            }
            responseJson(object.toString());
        }
    }

    private static double computePt(int div, double base, int rank) {
        return div / Math.exp(Math.log(rank) / Math.log(base));
    }

    private static Consumer<DiscRecord> computePtOfCd() {
        return record -> {
            record.setAdpt(computePt(150, 5.25, record.getRank()));
        };
    }

    private static Consumer<DiscRecord> computePtOfDvd() {
        return record -> {
            DiscType type = record.getDisc().getType();
            int rank = record.getRank();
            if (type == DiscType.BD || type == DiscType.BD_BOX) {
                if (rank <= 10) {
                    record.setAdpt(computePt(100, 3.2, rank));
                } else if (rank <= 20) {
                    record.setAdpt(computePt(100, 3.3, rank));
                } else if (rank <= 50) {
                    record.setAdpt(computePt(100, 3.4, rank));
                } else if (rank <= 100) {
                    record.setAdpt(computePt(100, 3.6, rank));
                } else if (rank <= 300) {
                    record.setAdpt(computePt(100, 3.8, rank));
                } else {
                    record.setAdpt(computePt(100, 3.9, rank));
                }
            } else if (type == DiscType.DVD || type == DiscType.DVD_BOX) {
                record.setAdpt(computePt(100, 4.2, rank));
            }
        };
    }

    private JSONArray buildRanks(Disc disc) {
        JSONArray array = new JSONArray();
        dao.execute(session -> {
            getRecords(disc, session).forEach(record -> {
                JSONObject object = new JSONObject();
                object.put("date", record.getDate().getTime() + 3600000);
                object.put("rank", record.getRank());
                array.put(object);
            });
        });
        return array;
    }

    private JSONArray buildRanksOfPt(Disc disc, Consumer<DiscRecord> consumer) {
        JSONArray array = new JSONArray();
        dao.execute(session -> {
            List<DiscRecord> records = getRecords(disc, session);
            computeRecordsPt(disc, records, consumer).forEach(record -> {
                JSONObject object = new JSONObject();
                object.put("date", record.getDate().getTime() + 3600000);
                object.put("rank", record.getRank());
                object.put("adpt", (int) (record.getAdpt() + 0.5));
                object.put("cupt", (int) (record.getCupt() + 0.5));
                array.put(object);
            });
        });
        return array;
    }

    public static Boolean isQiuDvd(Dao dao, Disc disc) {
        return dao.query(session -> {
            DiscList discList = dao.lookup(DiscList.class, "name", "mydvd");
            return discList != null && discList.getDiscs().contains(disc);
        });
    }

    public static List<DiscRecord> getRecords(Disc disc, Session session) {
        return session.createCriteria(DiscRecord.class)
                .add(Restrictions.eq("disc", disc))
                .addOrder(Order.desc("date"))
                .list();
    }

    public static List<DiscRecord> computeRecordsPtOfCd(Disc disc, List<DiscRecord> records) {
        return computeRecordsPt(disc, records, computePtOfCd());
    }

    public static List<DiscRecord> computeRecordsPtOfDvd(Disc disc, List<DiscRecord> records) {
        return computeRecordsPt(disc, records, computePtOfDvd());
    }

    public static List<DiscRecord> computeRecordsPt(Disc disc, List<DiscRecord> records, Consumer<DiscRecord> consumer) {
        Date date = new Date();
        date = DateUtils.addHours(date, -1);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMilliseconds(date, 0);

        List<DiscRecord> dest = new ArrayList<>(records.size() * 2);
        while (records.size() > 0) {
            Date release = DateUtils.addHours(disc.getRelease(), -1);
            DiscRecord discRecord = records.remove(0);
            while (date.compareTo(discRecord.getDate()) >= 0) {
                DiscRecord record = new DiscRecord();
                int rank = discRecord.getRank();
                record.setDisc(disc);
                record.setRank(rank);
                record.setDate(date);
                if (date.compareTo(release) < 0) {
                    consumer.accept(record);
                }
                dest.add(record);
                date = DateUtils.addHours(date, -1);
            }
        }

        double cupt = 0;
        for (int i = dest.size() - 1; i >= 0; i--) {
            DiscRecord record = dest.get(i);
            record.setCupt(cupt += record.getAdpt());
        }
        return dest;
    }

}
