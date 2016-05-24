package com.animediscs.action;

import com.animediscs.dao.Dao;
import com.animediscs.model.*;
import com.animediscs.support.BaseAction;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.animediscs.model.Disc.sortByAmazon;
import static com.animediscs.util.Helper.nullSafeGet;

public class DiscListAction extends BaseAction {

    private Dao dao;

    private String name;
    private String type;
    private String filter;
    private boolean latest;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public void list() throws Exception {
        if ("table".equals(filter)) {
            responseDiscList(findByTable());
        } else if ("type".equals(filter)) {
            listByType();
        } else {
            responseDiscList(findAll());
        }
    }

    private void listByType() throws IOException {
        if ("cd".equals(type)) {
            responseDiscList(findCd());
        } else {
            responseDiscList(findDvd());
        }
    }

    private void responseDiscList(DiscList discList) throws IOException {
        if (latest) {
            discList.getDiscs().removeIf(disc -> getSday(disc) < -14);
        }
        JSONObject object = new JSONObject();
        if (discList.getId() != null) {
            object.put("id", discList.getId());
        }
        setListTime(object, discList);
        object.put("name", discList.getName());
        object.put("title", discList.getTitle());
        object.put("discs", buildDiscList(discList));
        responseJson(object.toString());
    }

    private void setListTime(JSONObject object, DiscList discList) {
        Long time = discList.getDiscs().stream()
                .map(disc -> {
                    Date date = nullSafeGet(disc.getRank(), DiscRank::getPadt1);
                    return date == null ? 0 : date.getTime();
                }).sorted((o1, o2) -> o2.compareTo(o1))
                .findFirst().orElse(null);
        if (time != null) {
            object.put("time", time.longValue());
        }
    }

    private int getSday(Disc disc) {
        long currentTime = System.currentTimeMillis();
        long releaseTime = disc.getRelease().getTime() - 3600000L;
        return (int) ((releaseTime - currentTime) / 86400000L);
    }

    private DiscList findAll() {
        DiscList discList = new DiscList();
        discList.setName("all_disc");
        discList.setTitle("全部碟片");
        List<Disc> discs = dao.findAll(Disc.class);
        discs.sort(sortByAmazon());
        discList.setDiscs(discs);
        return discList;
    }

    private DiscList findByTable() throws IOException {
        DiscList discList = dao.lookup(DiscList.class, "name", name);
        if (discList != null) {
            dao.execute(session -> {
                List<Disc> discs = dao.get(DiscList.class, discList.getId()).getDiscs();
                discs.sort(sortByAmazon());
                if ("mycd".equals(name)) {
                    updateCdSakura(discs);
                }
                if ("mydvd".equals(name)) {
                    updateDvdSakura(discs);
                }
                discList.setDiscs(discs);
            });
        } else {
            responseError("未找到指定的动画列表");
        }
        return discList;
    }

    private DiscList findDvd() {
        DiscList discList = new DiscList();
        discList.setName("all_dvd");
        discList.setTitle("所有动画碟片");
        dao.execute(session -> {
            List<Disc> discs = session.createCriteria(Disc.class)
                    .add(Restrictions.ne("type", DiscType.CD))
                    .list();
            discs.sort(sortByAmazon());
            discList.setDiscs(discs);
        });
        return discList;
    }

    private DiscList findCd() {
        DiscList discList = new DiscList();
        discList.setName("all_cd");
        discList.setTitle("所有音乐碟片");
        dao.execute(session -> {
            List<Disc> discs = session.createCriteria(Disc.class)
                    .add(Restrictions.eq("type", DiscType.CD))
                    .list();
            discs.sort(sortByAmazon());
            updateCdSakura(discs);
            discList.setDiscs(discs);
        });
        return discList;
    }

    private void updateCdSakura(List<Disc> discs) {
        discs.forEach(disc -> {
            DiscSakura sakura = disc.getSakura();
            if (sakura == null) {
                sakura = new DiscSakura();
            }
            sakura.setDisc(disc);
            if (disc.getRank() != null) {
                sakura.setCurk(disc.getRank().getPark1());
                sakura.setPrrk(disc.getRank().getPark2());
            }
            sakura.setSday(getSday(disc));
            if (needUpdateCupt(sakura)) {
                sakura.setCupt(getCdCupt(disc));
            }
            sakura.setDate(new Date());
            dao.save(sakura);
        });
    }

    private void updateDvdSakura(List<Disc> discs) {
        discs.forEach(disc -> {
            DiscSakura sakura = disc.getSakura();
            if (sakura == null) {
                sakura = new DiscSakura();
            }
            sakura.setDisc(disc);
            if (disc.getRank() != null) {
                sakura.setCurk(disc.getRank().getPark1());
                sakura.setPrrk(disc.getRank().getPark2());
            }
            sakura.setSday(getSday(disc));
            if (needUpdateCupt(sakura)) {
                sakura.setCupt(getDvdCupt(disc));
            }
            sakura.setDate(new Date());
            dao.save(sakura);
        });
    }

    private boolean needUpdateCupt(DiscSakura sakura) {
        if (sakura.getDate() == null) {
            return true;
        }
        if (sakura.getSday() < 0) {
            return false;
        }
        Date date = new Date();
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        date = DateUtils.setMilliseconds(date, 0);
        return date.compareTo(sakura.getDate()) > 0;
    }

    private int getCdCupt(Disc disc) {
        return (int) (0.5 + dao.query(session -> {
            List<DiscRecord> records = RankAction.getRecords(disc, session);
            return RankAction.computeRecordsPtOfCd(disc, records).stream()
                    .mapToDouble(DiscRecord::getCupt)
                    .findFirst().orElse(0);
        }));
    }

    private int getDvdCupt(Disc disc) {
        return (int) (0.5 + dao.query(session -> {
            List<DiscRecord> records = RankAction.getRecords(disc, session);
            return RankAction.computeRecordsPtOfDvd(disc, records).stream()
                    .mapToDouble(DiscRecord::getCupt)
                    .findFirst().orElse(0);
        }));
    }

    private JSONArray buildDiscList(DiscList discList) {
        JSONArray array = new JSONArray();
        discList.getDiscs().forEach(disc -> {
            JSONObject object = new JSONObject();
            object.put("id", disc.getId());
            object.put("asin", disc.getAsin());
            object.put("title", disc.getTitle());
            object.put("type", disc.getType().ordinal());
            object.put("amzver", disc.isAmzver());
            if (disc.getSname() == null || disc.getSname().isEmpty()) {
                object.put("sname", disc.getTitle());
            } else {
                object.put("sname", disc.getSname());
            }
            if (disc.getRelease() != null) {
                object.put("release", disc.getRelease().getTime());
            }
            DiscRank rank = disc.getRank();
            if (rank != null) {
                if (discList.isTop100()) {
                    if (rank.getSpdt() != null) {
                        object.put("arnk", rank.getSpdt());
                        object.put("atot", rank.getSpdt().getTime());
                    }
                } else {
                    if (rank.getPadt() != null) {
                        object.put("arnk", rank.getPark());
                        object.put("atot", rank.getPadt().getTime());
                    }
                }
                if (rank.getPadt1() != null) {
                    if (rank.getPadt1() != null) {
                        object.put("acot", rank.getPadt1().getTime());
                    }
                    object.put("rank1", rank.getPark1());
                    object.put("rank2", rank.getPark2());
                    object.put("rank3", rank.getPark3());
                    object.put("rank4", rank.getPark4());
                    object.put("rank5", rank.getPark5());
                }
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
            } else if (disc.getRelease() != null) {
                object.put("sday", getSday(disc));
            }
            array.put(object);
        });
        return array;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

}
