package com.animediscs.action;

import com.animediscs.model.DiscList;
import com.animediscs.service.DiscService;
import com.animediscs.support.BaseAction;
import com.animediscs.support.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static com.animediscs.action.DiscAction.buildDisc;

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
        boolean top100 = "top_100".equals(discList.getName());
        JSONArray array = new JSONArray();
        discService.getDiscsOfDiscList(discList).forEach(disc -> {
            array.put(buildDisc(disc, top100));
        });
        object.put("discs", array);
        return object;
    }

}
