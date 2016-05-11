package fands.action;

import fands.dao.Dao;
import fands.model.disc.Disc;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;

public class DiscAction extends fands.support.JsonAction {

    private ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> {
        return new SimpleDateFormat("yyyy/MM/dd");
    });

    private Dao dao;
    private Long id;
    private String title;
    private String sname;
    private boolean dvdver;
    private boolean boxver;
    private boolean amzver;
    private String release;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
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
        if (disc.getRelease() != null) {
            object.put("release", disc.getRelease().getTime());
        }
        responseJson(object.toString());
    }

    public void update() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        disc.setTitle(title == null ? "" : title);
        disc.setSname(sname);
        disc.setDvdver(dvdver);
        disc.setBoxver(boxver);
        disc.setAmzver(amzver);
        disc.setRelease(sdf.get().parse(release));
        dao.update(disc);
        responseSuccess();
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

    public void setDvdver(boolean dvdver) {
        this.dvdver = dvdver;
    }

    public void setBoxver(boolean boxver) {
        this.boxver = boxver;
    }

    public void setAmzver(boolean amzver) {
        this.amzver = amzver;
    }

    public void setRelease(String release) {
        this.release = release;
    }

}
