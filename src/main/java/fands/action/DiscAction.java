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
    private String type;
    private boolean amzver;

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
        object.put("dvdver", disc.isDvdver());
        object.put("boxver", disc.isBoxver());
        object.put("amzver", disc.isAmzver());
        if (disc.getShelves() != null) {
            object.put("shelves", disc.getShelves().getTime());
        }
        if (disc.getRelease() != null) {
            object.put("release", disc.getRelease().getTime());
        }
        responseJson(object.toString());
    }

    public void update() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        disc.setTitle(title == null ? "" : title);
        disc.setSname(sname);
        dao.update(disc);
        responseSuccess();
    }

}
