package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "anime")
public class Anime extends BaseModel implements Comparable<Anime> {

    private String name;
    private Season season;
    private Series series;
    private Production production;

    @Column(length = 100, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(optional = false)
    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @ManyToOne
    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    @ManyToOne
    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public int compareTo(Anime other) {
        Assert.notNull(other);
        return name.compareTo(other.name);
    }

}
