package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.support.BaseAction;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.animediscs.util.Helper.getSday;
import static com.animediscs.util.Parser.parseDate;

public class DiscAction extends BaseAction {

    private Dao dao;

    private Long id;
    private String title;
    private String sname;
    private String type;
    private boolean amzver;
    private String release;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void view() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("title", disc.getTitle());
        object.put("japan", disc.getJapan());
        object.put("sname", disc.getSname());
        object.put("type", disc.getType().ordinal());
        object.put("amzver", disc.isAmzver());
        if (disc.getRelease() != null) {
            object.put("release", disc.getRelease().getTime());
        }
        DiscRank rank = disc.getRank();
        if (rank != null) {
            if (rank.getPadt1() != null) {
                object.put("rank1", rank.getPark1());
                object.put("date1", rank.getPadt1().getTime());
            }
            if (rank.getPadt2() != null) {
                object.put("rank2", rank.getPark2());
                object.put("date2", rank.getPadt2().getTime());
            }
            if (rank.getPadt3() != null) {
                object.put("rank3", rank.getPark3());
                object.put("date3", rank.getPadt3().getTime());
            }
            if (rank.getPadt4() != null) {
                object.put("rank4", rank.getPark4());
                object.put("date4", rank.getPadt4().getTime());
            }
            if (rank.getPadt5() != null) {
                object.put("rank5", rank.getPark5());
                object.put("date5", rank.getPadt5().getTime());
            }
        }
        DiscSakura sakura = disc.getSakura();
        if (sakura != null) {
            object.put("curk", sakura.getCurk());
            object.put("prrk", sakura.getPrrk());
            object.put("cupt", sakura.getCupt());
            object.put("cubk", sakura.getCubk());
            object.put("sday", sakura.getSday());
        } else if (disc.getRelease() != null) {
            object.put("sday", getSday(disc));
        }
        responseJson(object.toString());
    }

    public void edit() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date release = parseDate(dateFormat, this.release);

        Disc disc = dao.get(Disc.class, id);
        disc.setTitle(title);
        disc.setSname(sname);
        disc.setType(DiscType.valueOf(type));
        disc.setAmzver(amzver);
        disc.setRelease(release);
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

    public void setType(String type) {
        this.type = type;
    }

    public void setAmzver(boolean amzver) {
        this.amzver = amzver;
    }

    public void setRelease(String release) {
        this.release = release;
    }

}
