package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.Disc;
import com.animediscs.model.DiscRecord;
import com.animediscs.support.BaseAction;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
            if (disc.getType() != DiscType.CD) {
                object.put("ranks", buildRanks(disc));
            } else {
                object.put("ranks", buildRanks2(disc));
            }
            responseJson(object.toString());
        }
    }

    private JSONArray buildRanks(Disc disc) {
        JSONArray array = new JSONArray();
        dao.execute(session -> {
            session.createCriteria(DiscRecord.class)
                    .add(Restrictions.eq("disc", disc))
                    .addOrder(Order.desc("date"))
                    .list().forEach(o -> {
                DiscRecord record = (DiscRecord) o;
                JSONObject object = new JSONObject();
                object.put("date", record.getDate().getTime() + 3600000);
                object.put("rank", record.getRank());
                array.put(object);
            });
        });
        return array;
    }

    private JSONArray buildRanks2(Disc disc) {
        JSONArray array = new JSONArray();
        dao.execute(session -> {
            List<DiscRecord> list = session.createCriteria(DiscRecord.class)
                    .add(Restrictions.eq("disc", disc))
                    .addOrder(Order.desc("date"))
                    .list();
            Date date = new Date();
            date = DateUtils.addHours(date, -1);
            date = DateUtils.setMinutes(date, 0);
            date = DateUtils.setSeconds(date, 0);
            date = DateUtils.setMilliseconds(date, 0);

            List<DiscRecord> dest = new ArrayList<>(list.size() * 2);
            while (list.size() > 0) {
                Date release = DateUtils.addHours(disc.getRelease(), -1);
                DiscRecord discRecord = list.remove(0);
                while (date.compareTo(discRecord.getDate()) >= 0) {
                    DiscRecord record = new DiscRecord();
                    int rank = discRecord.getRank();
                    record.setRank(rank);
                    record.setDate(date);
                    if (date.compareTo(release) < 0) {
                        record.setAdpt(150 / Math.exp(Math.log(rank) / Math.log(5.25)));
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

            dest.forEach(record -> {
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


}
