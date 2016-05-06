package fands.model.volume;

import fands.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "volume")
public class Volume extends BaseModel implements Comparable<Volume> {

    private VolumeGroup group;
    private String name;

    private int fwdv; // first week dvd
    private int fwbd; // first week bd
    private int todv; // total dvd
    private int tobd; // total bd

    public Volume() {
    }

    @ManyToOne(optional = false)
    public VolumeGroup getGroup() {
        return group;
    }

    public void setGroup(VolumeGroup group) {
        this.group = group;
    }

    @Column(length = 100, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public int getFwdv() {
        return fwdv;
    }

    public void setFwdv(int fwdv) {
        this.fwdv = fwdv;
    }

    @Column
    public int getFwbd() {
        return fwbd;
    }

    public void setFwbd(int fwbd) {
        this.fwbd = fwbd;
    }

    @Column
    public int getTodv() {
        return todv;
    }

    public void setTodv(int todv) {
        this.todv = todv;
    }

    @Column
    public int getTobd() {
        return tobd;
    }

    public void setTobd(int tobd) {
        this.tobd = tobd;
    }

    public int compareTo(Volume other) {
        Assert.notNull(other);
        return name.compareTo(other.name);
    }

}
