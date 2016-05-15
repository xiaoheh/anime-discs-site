package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "anime")
public class Anime extends BaseModel implements Comparable<Anime> {

    private Season season;
    private String japan;
    private String title;
    private String sname;

    @OneToOne(optional = false)
    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @Column(length = 100, nullable = false, unique = true)
    public String getJapan() {
        return japan;
    }

    public void setJapan(String japan) {
        this.japan = japan;
    }

    @Column(length = 100, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 30)
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public int compareTo(Anime other) {
        Assert.notNull(other);
        return title.compareTo(other.title);
    }

}
