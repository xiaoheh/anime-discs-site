package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "season")
public class Season extends BaseModel implements Comparable<Season> {

    private String name;

    @Column(length = 100, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Season other) {
        Assert.notNull(other);
        return other.name.compareTo(name);
    }

}
