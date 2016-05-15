package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "disc")
public class Disc extends BaseModel implements Comparable<Disc> {

    private Anime anime;

    private String asin;
    private String title;
    private String japan;
    private String sname;

    private boolean dvdver;
    private boolean boxver;
    private boolean amzver;

    private Date release;
    private DiscRank rank;
    private DiscSakura sakura;

    @ManyToOne
    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

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

    @Column(name = "release_date")
    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    @OneToOne(mappedBy = "disc")
    public DiscRank getRank() {
        return rank;
    }

    public void setRank(DiscRank rank) {
        this.rank = rank;
    }

    @OneToOne(mappedBy = "disc")
    public DiscSakura getSakura() {
        return sakura;
    }

    public void setSakura(DiscSakura sakura) {
        this.sakura = sakura;
    }

    public int compareTo(Disc other) {
        Assert.notNull(other);
        if (sakura != null && other.sakura != null) {
            return sakura.compareTo(other.sakura);
        } else {
            return title.compareTo(other.title);
        }
    }

    public static String titleOfDisc(String discName) {
        discName = discName.replace("【Blu-ray】", " [Blu-ray]");
        discName = discName.replace("【DVD】", " [DVD]");
        if (isAmzver(discName)) {
            discName = discName.substring(16).trim() + "【尼限定】";
        }
        discName = discName.replaceAll("\\s+", " ");
        return discName;
    }

    public static boolean isAmzver(String japan) {
        return japan.startsWith("【Amazon.co.jp限定】");
    }

}
