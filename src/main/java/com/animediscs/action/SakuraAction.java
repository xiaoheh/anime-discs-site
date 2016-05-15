package com.animediscs.action;

import com.animediscs.model.*;
import com.animediscs.service.DiscService;
import com.animediscs.support.BaseAction;
import com.animediscs.support.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class SakuraAction extends BaseAction {

    private static Cache<String> index = new Cache<>(3000);

    private DiscService discService;

    @Autowired
    public void setDiscService(DiscService discService) {
        this.discService = discService;
    }

    public void get() throws Exception {
        String text = index.update(() -> {
            JSONArray array = new JSONArray();
            discService.findLatestDiscList().forEach(discList -> {
                array.put(buildDiscList(discList));
            });
            return array.toString();
        });
        responseJson(text);
    }

    private JSONObject buildDiscList(DiscList discList) {
        JSONObject object = new JSONObject();
        object.put("key", discList.getName());
        object.put("title", discList.getTitle());
        if (discList.getDate() != null) {
            object.put("time", discList.getDate().getTime());
        }
        object.put("discs", buildDiscs(discList));
        return object;
    }

    private JSONArray buildDiscs(DiscList discList) {
        boolean top100 = "top_100".equals(discList.getName());
        JSONArray array = new JSONArray();
        discService.getDiscsOfDiscList(discList).forEach(disc -> {
            array.put(buildDisc(disc, top100));
        });
        return array;
    }

    private JSONObject buildDisc(Disc disc, boolean top100) {
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("title", disc.getTitle());
        object.put("japan", disc.getJapan());
        object.put("dvdver", disc.isDvdver());
        object.put("boxver", disc.isBoxver());
        object.put("amzver", disc.isAmzver());
        if (disc.getSname() == null) {
            object.put("sname", disc.getTitle());
        } else {
            object.put("sname", disc.getSname());
        }
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
                object.put("date1", rank.getPadt1());
                object.put("rank1", rank.getPark1());
            }
            if (rank.getPadt2() != null) {
                object.put("date2", rank.getPadt2());
                object.put("rank2", rank.getPark2());
            }
            if (rank.getPadt3() != null) {
                object.put("date3", rank.getPadt3());
                object.put("rank3", rank.getPark3());
            }
            if (rank.getPadt4() != null) {
                object.put("date4", rank.getPadt4());
                object.put("rank4", rank.getPark4());
            }
            if (rank.getPadt5() != null) {
                object.put("date5", rank.getPadt5());
                object.put("rank6", rank.getPark5());
            }
        }
        DiscSakura sakura = disc.getSakura();
        if (sakura != null) {
            object.put("curk", sakura.getCurk());
            object.put("prrk", sakura.getPrrk());
            object.put("cupt", sakura.getCupt());
            object.put("cubk", sakura.getCubk());
            object.put("sday", sakura.getSday());
        }
        return object;
    }

}
