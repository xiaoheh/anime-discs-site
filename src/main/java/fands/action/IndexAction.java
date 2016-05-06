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
            object.put("lists", array);
            return object.toString();
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
        AtomicInteger count = new AtomicInteger(0);
        boolean top100 = TOP_100_NAME.equals(discList.getName());

        JSONArray array = new JSONArray();
        discService.getDiscsOfDiscList(discList).forEach(disc -> {
            array.put(buildDisc(disc, count.incrementAndGet(), top100));
        });
        return array;
    }

    private JSONObject buildDisc(Disc disc, int index, boolean top100) {
        JSONObject object = new JSONObject();
        object.put("id", disc.getId());
        object.put("index", index);
        object.put("sname", disc.getTitle());
        DiscAmazon amazon = disc.getAmazon();
        if (amazon != null) {
            if (top100) {
                if (amazon.getSpdt() != null) {
                    object.put("arnk", amazon.getSprk());
                }
            } else {
                if (amazon.getPadt() != null) {
                    object.put("arnk", amazon.getPark());
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
            }
        }
        return object;
    }

}
