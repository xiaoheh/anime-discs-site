package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.support.BaseAction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static com.animediscs.util.Helper.readAllLines;

public class DiscAction extends BaseAction {

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
    private String filter;
    private String name;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void get() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        JSONObject object = buildDisc(disc, false);
        responseJson(object.toString());
    }

    public static JSONObject buildDisc(Disc disc, boolean top100) {
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
        DiscRank rank = disc.getRank();
        if (rank != null) {
            if (top100) {
                if (rank.getSpdt() != null) {
                    object.put("arnk", rank.getSprk());
                    object.put("amdt", rank.getSpdt().getTime());
                }
            } else {
                if (rank.getPadt() != null) {
                    object.put("arnk", rank.getPark());
                    object.put("amdt", rank.getPadt().getTime());
                }
            }
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
        return object;
    }

    private static int getSday(Disc disc) {
        long currentTime = System.currentTimeMillis();
        long releaseTime = disc.getRelease().getTime() - 3600000L;
        return (int) ((releaseTime - currentTime) / 86400000L);
    }

    public void list() throws Exception {
        if ("disclist".equals(filter) && "manual".equals(name)) {
            DiscList discList = new DiscList();
            discList.setName("manual");
            discList.setTitle("手动维护的动画列表");
            readAllLines("config/disclist.txt").forEach(asin -> {
                Disc disc = dao.lookup(Disc.class, "asin", asin);
                if (disc != null) {
                    discList.getDiscs().add(disc);
                }
            });
            Collections.sort(discList.getDiscs());

            JSONArray array = new JSONArray();
            array.put(buildDiscList(discList));
            responseJson(array.toString());
        } else {
            responseError("不支持的操作");
        }
    }

    public void listRank() throws Exception {
        Disc disc = dao.get(Disc.class, id);
        if (disc == null) {
            responseError("没有这个碟片数据");
        } else {
            JSONObject object = buildDisc(disc, false);
            object.put("ranks", buildRanks(dao.findBy(DiscRecord.class, "disc", disc)));
            responseJson(object.toString());
        }
    }

    private JSONArray buildRanks(List<DiscRecord> ranks) {
        JSONArray array = new JSONArray();
        ranks.stream().sorted().forEach(rank -> {
            JSONObject object = new JSONObject();
            object.put("date", rank.getDate().getTime() + 3600000);
            object.put("rank", rank.getRank());
            array.put(object);
        });
        return array;
    }

    private JSONObject buildDiscList(DiscList discList) {
        JSONObject object = new JSONObject();
        object.put("key", discList.getName());
        object.put("title", discList.getTitle());
        if (discList.getDate() != null) {
            object.put("time", discList.getDate().getTime());
        }
        JSONArray array = new JSONArray();
        discList.getDiscs().forEach(disc -> {
            array.put(buildDisc(disc, false));
        });
        object.put("discs", array);
        return object;
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

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setName(String name) {
        this.name = name;
    }

}
