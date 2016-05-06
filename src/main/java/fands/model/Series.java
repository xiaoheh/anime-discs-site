package fands.model;

import fands.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "series")
public class Series extends BaseModel implements Comparable<Series> {

    private String name;

    @Column(length = 100, nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Series other) {
        Assert.notNull(other);
        return name.compareTo(other.name);
    }

}
