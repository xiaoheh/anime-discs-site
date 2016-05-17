package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.Disc;
import com.animediscs.model.DiscRecord;
import com.animediscs.support.BaseAction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

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
            object.put("ranks", buildRanks(disc));
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

}
