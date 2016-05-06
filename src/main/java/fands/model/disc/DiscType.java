package fands.model.disc;

import fands.support.HelpUtil;

import java.util.Arrays;
import java.util.Objects;

public enum DiscType {

    BD("Blu-ray", "★"), DVD("DVD", "○"), BOX("BOX", "◎");

    private final String title;
    private final String icon;

    DiscType(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public static DiscType valueOfIcon(String icon) {
        return Arrays.stream(values()).filter(t -> Objects.equals(t.icon, icon)).findAny()
                .orElseThrow(() -> HelpUtil.newError("找不到该图标所属的碟片类型: icon=", icon));
    }

}
