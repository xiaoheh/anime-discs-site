package fands.model.disc;

import fands.model.Anime;
import fands.model.Season;
import fands.model.volume.Volume;
import fands.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "disc")
public class Disc extends BaseModel implements Comparable<Disc> {

    private String asin;
    private String title;
    private String japan;
    private String sname;

    private boolean dvdver;
    private boolean boxver;
    private boolean amzver;

    private Anime anime;
    private Volume volume;
    private Season season;

    private Date shelves;
    private Date release;
    private DiscSakura sakura;
    private DiscAmazon amazon;

    @Column(length = 20, nullable = false, unique = true)
    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    @Column(length = 500, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 500, nullable = false)
    public String getJapan() {
        return japan;
    }

    public void setJapan(String japan) {
        this.japan = japan;
    }

    @Column(length = 30)
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    @Column
    public boolean isDvdver() {
        return dvdver;
    }

    public void setDvdver(boolean dvdver) {
        this.dvdver = dvdver;
    }

    @Column
    public boolean isBoxver() {
        return boxver;
    }

    public void setBoxver(boolean boxver) {
        this.boxver = boxver;
    }

    @Column
    public boolean isAmzver() {
        return amzver;
    }

    public void setAmzver(boolean amzver) {
        this.amzver = amzver;
    }

    @ManyToOne
    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    @ManyToOne
    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    @ManyToOne
    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @Column(name = "shelves_date")
    public Date getShelves() {
        return shelves;
    }

    public void setShelves(Date shelves) {
        this.shelves = shelves;
    }

    @Column(name = "release_date")
    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    @OneToOne(mappedBy = "disc")
    public DiscSakura getSakura() {
        return sakura;
    }

    public void setSakura(DiscSakura sakura) {
        this.sakura = sakura;
    }

    @OneToOne(mappedBy = "disc")
    public DiscAmazon getAmazon() {
        return amazon;
    }

    public void setAmazon(DiscAmazon amazon) {
        this.amazon = amazon;
    }

    public int compareTo(Disc other) {
        Assert.notNull(other);
        if (sakura != null && other.sakura != null) {
            return sakura.compareTo(other.sakura);
        } else {
            return title.compareTo(other.title);
        }
    }

}
