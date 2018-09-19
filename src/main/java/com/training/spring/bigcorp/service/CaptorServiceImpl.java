package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaptorServiceImpl implements CaptorService{

    private MeasureService fixedMeasureService;
    private MeasureService realMeasureService;
    private MeasureService simulatedMeasureService;
    private CaptorDao captorDao;

    @Autowired
    public CaptorServiceImpl(MeasureService fixedMeasureService, MeasureService realMeasureService, MeasureService simulatedMeasureService, CaptorDao captorDao) {
        this.fixedMeasureService = fixedMeasureService;
        this.realMeasureService = realMeasureService;
        this.simulatedMeasureService = simulatedMeasureService;
        this.captorDao = captorDao;
    }

    @Override
    @Monitored
    public Set<Captor> findBySite(String siteId) {
        if (siteId == null) {
            return new HashSet<>();
        }
        return captorDao.findBySiteId(siteId).stream().collect(Collectors.toSet());
    }
}
