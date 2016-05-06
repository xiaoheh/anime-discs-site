package fands.model.disc;

import fands.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dics_sakura")
public class DiscSakura extends BaseModel implements Comparable<DiscSakura> {

    private Disc disc;

    private Date spdt; // speed date
    private int curk; // current spdt
    private int prrk; // previous spdt

    private Date padt; // page date
    private int cubk; // current book
    private int prbk; // previous book
    private int cupt; // current point
    private int prpt; // previous point
    private int capt; // calculate point
    private int sday; // surplus days

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
    public int getCurk() {
        return curk;
    }

    public void setCurk(int curk) {
        this.curk = curk;
    }

    @Column
    public int getPrrk() {
        return prrk;
    }

    public void setPrrk(int prrk) {
        this.prrk = prrk;
    }

    @Column
    public Date getPadt() {
        return padt;
    }

    public void setPadt(Date padt) {
        this.padt = padt;
    }

    @Column
    public int getCubk() {
        return cubk;
    }

    public void setCubk(int cubk) {
        this.cubk = cubk;
    }

    @Column
    public int getPrbk() {
        return prbk;
    }

    public void setPrbk(int prbk) {
        this.prbk = prbk;
    }

    @Column
    public int getCupt() {
        return cupt;
    }

    public void setCupt(int cupt) {
        this.cupt = cupt;
    }

    @Column
    public int getPrpt() {
        return prpt;
    }

    public void setPrpt(int prpt) {
        this.prpt = prpt;
    }

    @Column
    public int getCapt() {
        return capt;
    }

    public void setCapt(int capt) {
        this.capt = capt;
    }

    @Column
    public int getSday() {
        return sday;
    }

    public void setSday(int sday) {
        this.sday = sday;
    }

    @Transient
    public int getTapt() {
        return cupt - prpt;
    }

    public int compareTo(DiscSakura other) {
        Assert.notNull(other);
        return curk - other.curk;
    }

}
