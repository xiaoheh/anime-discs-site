package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.Disc;
import com.animediscs.model.DiscList;
import com.animediscs.runner.AutoRunner;
import com.animediscs.spider.AmazonAnimeSpider;
import com.animediscs.support.BaseAction;
import org.hibernate.criterion.Order;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TableAction extends BaseAction {

    private Dao dao;
    private AutoRunner autoRunner;
    private AmazonAnimeSpider amazonAnimeSpider;

    private Long id;
    private Long discId;
    private String asin;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setAutoRunner(AutoRunner autoRunner) {
        this.autoRunner = autoRunner;
    }

    @Autowired
    public void setAmazonAnimeSpider(AmazonAnimeSpider amazonAnimeSpider) {
        this.amazonAnimeSpider = amazonAnimeSpider;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDiscId(Long discId) {
        this.discId = discId;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }


    public void view() throws Exception {
        DiscList discList = dao.get(DiscList.class, id);
        JSONObject object = new JSONObject();
        object.put("id", discList.getId());
        object.put("name", discList.getName());
        object.put("title", discList.getTitle());
        object.put("sakura", discList.isSakura());
        responseJson(object.toString());
    }

    public void list() throws Exception {
        JSONArray array = new JSONArray();
        dao.execute(session -> {
            session.createCriteria(DiscList.class)
                    .addOrder(Order.desc("sakura"))
                    .addOrder(Order.desc("title"))
                    .list().forEach(o -> {
                DiscList discList = (DiscList) o;
                JSONObject object = new JSONObject();
                object.put("id", discList.getId());
                object.put("name", discList.getName());
                object.put("title", discList.getTitle());
                array.put(object);
            });
        });
        responseJson(array.toString());
    }

    public void removeDisc() throws Exception {
        dao.execute(session -> {
            dao.get(DiscList.class, id).getDiscs().removeIf(disc -> {
                return disc.getId().equals(discId);
            });
        });
        responseSuccess();
    }

    public void addDiscWithId() throws Exception {
        JSONObject object = new JSONObject();
        dao.execute(session -> {
            Disc disc = dao.get(Disc.class, discId);
            if (disc != null) {
                dao.get(DiscList.class, id).getDiscs().add(disc);
                object.put("disc", buildDisc(disc));
                object.put("success", true);
            } else {
                object.put("error", "未找到指定碟片");
                object.put("success", false);
            }
        });
        responseJson(object.toString());
    }

    public void addDiscWithAsin() throws Exception {
        JSONObject object = new JSONObject();
        dao.execute(session -> {
            Disc disc = dao.lookup(Disc.class, "asin", asin);
            if (disc != null) {
                dao.get(DiscList.class, id).getDiscs().add(disc);
                object.put("disc", buildDisc(disc));
                object.put("success", true);
            } else {
                amazonAnimeSpider.doUpdate(autoRunner.getAmazonRunner(), 3, id, asin);
                object.put("error", "未找到指定碟片, 已安排从亚马逊查询, 查询成功后会自动添加到该榜单");
                object.put("success", false);
            }
        });
        responseJson(object.toString());
    }

    private JSONObject buildDisc(Disc disc) {
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("title", disc.getTitle());
        return object;
    }

}
