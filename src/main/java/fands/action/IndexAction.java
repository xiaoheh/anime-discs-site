package fands.action;

import fands.model.DiscList;
import fands.model.disc.*;
import fands.service.DiscService;
import fands.support.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

import static fands.support.Constants.TOP_100_NAME;
import static fands.support.HelpUtil.*;

public class IndexAction extends fands.support.JsonAction {

    private static Cache<String> index = new Cache<>(3000);

    private DiscService discService;

    @Autowired
    public void setDiscService(DiscService discService) {
        this.discService = discService;
    }

    public void index() throws Exception {
        String text = index.update(() -> {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            discService.findLatestDiscList().forEach(discList -> {
                array.put(buildDiscList(discList));
            });
            object.put("tables", array);
            return object.toString();
        });
        responseJson(text);
    }

    private JSONObject buildDiscList(DiscList discList) {
        JSONObject object = new JSONObject();
        object.put("name", discList.getName());
        object.put("title", discList.getTitle());
        if (discList.getDate() != null) {
            object.put("update", formatUpdate(discList.getDate()));
            object.put("timeout", formatTimeout(discList.getDate()));
        }
        object.put("discs", buildDiscs(discList));
        return object;
    }

    private JSONArray buildDiscs(DiscList discList) {
        AtomicInteger count = new AtomicInteger(0);
        long current = System.currentTimeMillis();
        boolean top100 = TOP_100_NAME.equals(discList.getName());

        JSONArray array = new JSONArray();
        discService.getDiscsOfDiscList(discList).forEach(disc -> {
            array.put(buildDisc(disc, count.incrementAndGet(), current, top100));
        });
        return array;
    }

    private JSONObject buildDisc(Disc disc, int index, long current, boolean top100) {
        JSONObject object = new JSONObject();
        object.put("index", index);
        object.put("id", disc.getId());
        object.put("asin", disc.getAsin());
        object.put("name", disc.getName());
        object.put("title", disc.getTitle());
        appendDiscType(object, disc);
        appendShelves(object, disc);
        appendRelease(object, disc);
        appendAmazon(object, disc, current, top100);
        appendSakura(object, disc, current);
        return object;
    }

    private void appendDiscType(JSONObject object, Disc disc) {
        if (disc.getType() != null) {
            object.put("type", disc.getType().ordinal());
            object.put("fmtype", disc.getType().getIcon());
        }
    }

    private void appendShelves(JSONObject object, Disc disc) {
        if (disc.getShelves() != null) {
            object.put("shelves", disc.getShelves().getTime());
            object.put("fmshelves", formatDisc(disc.getShelves()));
        }
    }

    private void appendRelease(JSONObject object, Disc disc) {
        if (disc.getRelease() != null) {
            object.put("release", disc.getRelease().getTime());
            object.put("fmrelease", formatDisc(disc.getRelease()));
        }
    }

    private void appendAmazon(JSONObject object, Disc disc, long current, boolean top100) {
        DiscAmazon amazon = disc.getAmazon();
        if (amazon != null) {
            if (top100) {
                if (amazon.getSpdt() != null) {
                    object.put("arnk", amazon.getSprk());
                    object.put("fmarnk", formatNumber("%s位", amazon.getSprk()));
                    object.put("atot", current - amazon.getSpdt().getTime());
                    object.put("fmatot", formatTimeout(amazon.getSpdt()));
                }
            } else {
                if (amazon.getPadt() != null) {
                    object.put("arnk", amazon.getPark());
                    object.put("fmarnk", formatNumber("%s位", amazon.getPark()));
                    object.put("atot", current - amazon.getPadt().getTime());
                    object.put("fmatot", formatTimeout(amazon.getPadt()));
                }
            }
        }
    }

    private void appendSakura(JSONObject object, Disc disc, long current) {
        DiscSakura sakura = disc.getSakura();
        if (sakura != null) {
            if (sakura.getSpdt() != null) {
                object.put("srnk", sakura.getCurk());
                String fmcurk = formatSakura("%s位", sakura.getCurk(), 7);
                String fmprrk = formatSakura("%s位", sakura.getPrrk(), 7);
                object.put("fmsrnk", fmcurk + "/" + fmprrk);
            }
            if (sakura.getPadt() != null) {
                object.put("cupt", sakura.getCupt());
                object.put("fmcupt", formatSakura("(%s pt)", sakura.getCupt(), 7));
                object.put("capt", sakura.getCapt());
                object.put("fmcapt", formatNumber("%s pt", sakura.getCapt()));
                object.put("tapt", sakura.getTapt());
                object.put("fmtapt", formatNumber("%s pt", sakura.getTapt()));
                object.put("sday", sakura.getSday());
                object.put("fmsday", sakura.getSday() + "天");
                object.put("cubk", sakura.getCubk());
                object.put("fmcubk", formatSakura("[%s预约]", sakura.getCubk(), 5));
                object.put("stot", current - sakura.getPadt().getTime());
                object.put("fmstot", formatTimeout(sakura.getPadt()));
            }
        }
    }

}
