package com.animediscs.action;

import com.animediscs.model.DiscList;
import com.animediscs.model.disc.*;
import com.animediscs.service.DiscService;
import com.animediscs.support.Cache;
import com.animediscs.support.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class SakuraAction extends JsonAction {

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
        boolean top100 = Constants.TOP_100_NAME.equals(discList.getName());
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
        DiscAmazon amazon = disc.getAmazon();
        if (amazon != null) {
            if (top100) {
                if (amazon.getSpdt() != null) {
                    object.put("arnk", amazon.getSprk());
                    object.put("amdt", amazon.getSpdt().getTime());
                }
            } else {
                if (amazon.getPadt() != null) {
                    object.put("arnk", amazon.getPark());
                    object.put("amdt", amazon.getPadt().getTime());
                }
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
