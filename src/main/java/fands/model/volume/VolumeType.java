package fands.model.volume;

public enum VolumeType {

    FirstSaleDisc("先发分卷"),

    FirstSaleBox("先发BOX"),

    LaterSaleDisc("后发分卷"),

    LaterSaleBox("后发BOX");

    private final String title;

    VolumeType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title;
    }

}
