package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.support.BaseAction;
import com.animediscs.support.Cache;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class SakuraAction extends BaseAction {

    private static Cache<String> index = new Cache<>(3000);

    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void view() throws Exception {
        String text = index.update(() -> {
            JSONArray array = new JSONArray();
            dao.execute(session -> {
                session.createCriteria(DiscList.class)
                        .add(Restrictions.eq("sakura", true))
                        .addOrder(Order.desc("name"))
                        .list().forEach(o -> {
                    array.put(buildDiscList((DiscList) o));
                });
            });
            return array.toString();
        });
        responseJson(text);
    }

    private JSONObject buildDiscList(DiscList discList) {
        boolean top100 = discList.isTop100();

        JSONObject object = new JSONObject();
        object.put("id", discList.getId());
        object.put("name", discList.getName());
        object.put("title", discList.getTitle());
        object.put("time", discList.getDate().getTime());
        JSONArray array = new JSONArray();
        discList.getDiscs().stream().sorted(Disc.sortBySakura()).forEach(disc -> {
            array.put(buildDisc(disc, top100));
        });
        object.put("discs", array);
        return object;
    }

    private JSONObject buildDisc(Disc disc, boolean top100) {
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("title", disc.getTitle());
        if (disc.getSname() == null || disc.getSname().isEmpty()) {
            object.put("sname", disc.getTitle());
        } else {
            object.put("sname", disc.getSname());
        }
        object.put("type", disc.getType().ordinal());
        object.put("amzver", disc.isAmzver());
        object.put("release", disc.getRelease().getTime());
        DiscRank rank = disc.getRank();
        if (rank != null) {
            if (rank.getPadt() != null) {
                object.put("atot", rank.getPadt().getTime());
            }
            if (rank.getPadt1() != null) {
                object.put("acot", rank.getPadt1().getTime());
            }
            object.put("rank1", rank.getPark1());
            object.put("rank2", rank.getPark2());
            object.put("rank3", rank.getPark3());
            object.put("rank4", rank.getPark4());
            object.put("rank5", rank.getPark5());
        }
        DiscSakura sakura = disc.getSakura();
        if (sakura != null) {
            if (sakura.getDate() != null) {
                object.put("stot", sakura.getDate().getTime());
            }
            object.put("curk", sakura.getCurk());
            object.put("prrk", sakura.getPrrk());
            object.put("cupt", sakura.getCupt());
            object.put("cubk", sakura.getCubk());
            object.put("sday", sakura.getSday());
        }
        return object;
    }

}
