package com.animediscs.model.disc;

import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "disc_amazon")
public class DiscAmazon extends BaseModel implements Comparable<DiscAmazon> {

    private Disc disc;

    private Date spdt; // speed date
    private int sprk; // speed rank

    private Date padt; // page date
    private int park; // page rank

    @OneToOne(optional = false)
    public Disc getDisc() {
        return disc;
    }

    public void setDisc(Disc disc) {
        this.disc = disc;
    }

    @Column
    public Date getSpdt() {
        return spdt;
    }

    public void setSpdt(Date spdt) {
        this.spdt = spdt;
    }

    @Column
    public int getSprk() {
        return sprk;
    }

    public void setSprk(int sprk) {
        this.sprk = sprk;
    }

    @Column
    public Date getPadt() {
        return padt;
    }

    public void setPadt(Date padt) {
        this.padt = padt;
    }

    @Column
    public int getPark() {
        return park;
    }

    public void setPark(int park) {
        this.park = park;
    }

    public int compareTo(DiscAmazon other) {
        Assert.notNull(other);
        return this.park - other.park;
    }

}
