package com.animediscs.model;

import com.animediscs.support.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "season")
public class Season extends BaseModel {

    private String japan;
    private String title;

    @Column(length = 30, nullable = false, unique = true)
    public String getJapan() {
        return japan;
    }

    public void setJapan(String japan) {
        this.japan = japan;
    }

    @Column(length = 30, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
