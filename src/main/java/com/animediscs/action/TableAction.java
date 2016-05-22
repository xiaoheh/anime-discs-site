package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.Disc;
import com.animediscs.model.DiscList;
import com.animediscs.runner.AutoRunner;
import com.animediscs.spider.AmazonDiscSpider;
import com.animediscs.support.BaseAction;
import org.hibernate.criterion.Order;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TableAction extends BaseAction {

    private Dao dao;
    private AutoRunner autoRunner;
    private AmazonDiscSpider amazonDiscSpider;

    private Long id;
    private Long discId;
    private String asin;
    private String name;
    private String title;
    private boolean sakura;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Autowired
    public void setAutoRunner(AutoRunner autoRunner) {
        this.autoRunner = autoRunner;
    }

    @Autowired
    public void setAmazonDiscSpider(AmazonDiscSpider amazonDiscSpider) {
        this.amazonDiscSpider = amazonDiscSpider;
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

    public void edit() throws Exception {
        DiscList discList = dao.get(DiscList.class, id);
        discList.setName(name);
        discList.setTitle(title);
        discList.setSakura(sakura);
        dao.update(discList);
        responseSuccess();
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
                amazonDiscSpider.doCreateDisc(autoRunner.getRankerRunner(), 1, id, asin);
                object.put("error", "未找到指定碟片, 已安排从亚马逊查询, 查询成功后会自动添加到该碟片列表");
                object.put("success", false);
            }
        });
        responseJson(object.toString());
    }

    private JSONObject buildDisc(Disc disc) {
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("title", disc.getTitle());
        return object;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSakura(boolean sakura) {
        this.sakura = sakura;
    }

}
