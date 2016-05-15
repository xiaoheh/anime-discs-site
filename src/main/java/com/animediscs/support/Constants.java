package com.animediscs.support;

import com.animediscs.model.DiscList;

public abstract class Constants {

    public static final DiscList ALL_DISCS;

    static {
        ALL_DISCS = new DiscList();
        ALL_DISCS.setName("all_discs");
        ALL_DISCS.setTitle("所有碟片");
    }

}
