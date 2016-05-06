package fands.support;

import fands.model.DiscList;

public abstract class Constants {

    public static final DiscList TOP_100;
    public static final String TOP_100_NAME = "top_100";
    public static final String TOP_100_TITLE = "日亚实时TOP100";

    public static final DiscList ALL_DISCS;
    public static final String ALL_DISCS_NAME = "all_discs";
    public static final String ALL_DISCS_TITLE = "所有碟片";


    static {
        TOP_100 = new DiscList();
        TOP_100.setName(TOP_100_NAME);
        TOP_100.setTitle(TOP_100_TITLE);

        ALL_DISCS = new DiscList();
        ALL_DISCS.setName(ALL_DISCS_NAME);
        ALL_DISCS.setTitle(ALL_DISCS_TITLE);
    }

}
