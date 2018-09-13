package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CaptorServiceImpl implements CaptorService{

    private MeasureService fixedMeasureService;
    private MeasureService realMeasureService;
    private MeasureService simulatedMeasureService;

    public CaptorServiceImpl(){}

    @Autowired
    public CaptorServiceImpl(@Qualifier("fixedMeasureService") MeasureService fixMeasService, MeasureService realMeasureService, MeasureService simulatedMeasureService) {
        this.fixedMeasureService = fixedMeasureService;
        this.realMeasureService = realMeasureService;
        this.simulatedMeasureService = simulatedMeasureService;
    }

    @Override
    @Monitored
    public Set<Captor> findBySite(String siteId) {
        Set<Captor> captors = new HashSet<>();
        if (siteId == null) {
            return captors;
        }
        captors.add(new Captor("Capteur A", null));
        return captors;
    }
}
