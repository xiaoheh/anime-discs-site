package com.animediscs.model;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "disc_record")
public class DiscRecord extends BaseModel implements Comparable<DiscRecord> {

    private Disc disc;
    private Date date; // used field: yyyy-MM-dd-HH
    private int rank; // this hour rank

    @ManyToOne(optional = false)
    public Disc getDisc() {
        return disc;
    }

    public void setDisc(Disc disc) {
        this.disc = disc;
    }

    @Column(nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int compareTo(DiscRecord other) {
        Assert.notNull(other);
        return this.date.compareTo(other.date);
    }

}
