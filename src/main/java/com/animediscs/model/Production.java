package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "production")
public class Production extends BaseModel implements Comparable<Production> {

    private String name;

    @Column(length = 100, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Production other) {
        Assert.notNull(other);
        return name.compareTo(other.name);
    }

}
