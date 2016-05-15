package com.animediscs.service;

import com.animediscs.dao.Dao;
import com.animediscs.model.DiscList;
import com.animediscs.model.disc.Disc;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscService {

    private Dao dao;

    @Autowired
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    @Transactional
    public DiscList getLatestDiscList() {
        return dao.findAll(DiscList.class).stream()
                .filter(this::isLatest).sorted()
                .skip(1).findFirst().orElse(null);
    }

    @Transactional
    public List<DiscList> findLatestDiscList() {
        return dao.findAll(DiscList.class).stream()
                .filter(this::isLatest).sorted()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<DiscList> findLatestDiscExtList() {
        return dao.findAll(DiscList.class).stream()
                .filter(this::isLatest).sorted()
                .skip(1).collect(Collectors.toList());
    }

    @Transactional
    public List<Disc> getDiscsOfDiscList(DiscList discList) {
        return dao.get(DiscList.class, discList.getId()).getDiscs()
                .stream().sorted().collect(Collectors.toList());
    }

    @Transactional
    public List<Disc> getDiscsOfDiscList(DiscList discList, int limit) {
        return dao.get(DiscList.class, discList.getId()).getDiscs()
                .stream().sorted().limit(limit).collect(Collectors.toList());
    }

    @Transactional
    public List<Disc> findAllDiscs() {
        return dao.findAll(Disc.class).stream().sorted().collect(Collectors.toList());
    }

    private boolean isLatest(DiscList discList) {
        Date yesterday = DateUtils.addDays(new Date(), -1);
        return discList.getDate() != null && discList.getDate().compareTo(yesterday) > 0;
    }

}
