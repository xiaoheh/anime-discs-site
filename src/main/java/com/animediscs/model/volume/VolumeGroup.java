package com.animediscs.model.volume;

import com.animediscs.model.Anime;
import com.animediscs.support.BaseModel;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "volume_group")
public class VolumeGroup extends BaseModel implements Comparable<VolumeGroup> {

    private Anime anime;
    private String name;
    private VolumeType type;

    @ManyToOne(optional = false)
    public Anime getAnime() {
        return anime;
    }

    public void setAnime(Anime anime) {
        this.anime = anime;
    }

    @Column(length = 100, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public VolumeType getType() {
        return type;
    }

    public void setType(VolumeType type) {
        this.type = type;
    }

    public int compareTo(VolumeGroup other) {
        Assert.notNull(other);
        return name.compareTo(other.name);
    }

}
