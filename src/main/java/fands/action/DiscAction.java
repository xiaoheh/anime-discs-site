package fands.action;

import fands.dao.Dao;
import fands.model.disc.Disc;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscAction extends fands.support.JsonAction {

    private Dao dao;
    private Long id;
    private String title;
    private String sname;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public void get() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("title", disc.getTitle());
        object.put("japan", disc.getJapan());
        object.put("sname", disc.getSname());
        responseJson(object.toString());
    }

    public void update() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        if (title != null) {
            disc.setTitle(title);
        }
        if (sname != null) {
            disc.setSname(sname);
        }
        dao.update(disc);
        responseSuccess();
    }

}
