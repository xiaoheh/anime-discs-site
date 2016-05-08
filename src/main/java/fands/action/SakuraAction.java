package fands.action;

import fands.model.DiscList;
import fands.model.disc.*;
import fands.service.DiscService;
import fands.support.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static fands.support.Constants.TOP_100_NAME;

public class SakuraAction extends fands.support.JsonAction {

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
        boolean top100 = TOP_100_NAME.equals(discList.getName());
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
        if (disc.getShelves() != null) {
            object.put("shelves", disc.getShelves().getTime());
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
            if (sakura.getSpdt() != null) {
                object.put("curk", sakura.getCurk());
                object.put("prrk", sakura.getPrrk());
            }
            if (sakura.getPadt() != null) {
                object.put("cupt", sakura.getCupt());
                object.put("capt", sakura.getCapt());
                object.put("tapt", sakura.getTapt());
                object.put("sday", sakura.getSday());
                object.put("cubk", sakura.getCubk());
                object.put("skdt", sakura.getPadt().getTime());
            }
        }
        return object;
    }

}
