package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class RealMeasureService implements MeasureService<RealCaptor>{

    @Autowired
    private BigCorpApplicationProperties bigCorpApplicationProperties;

    @Override public List<Measure> readMeasures(RealCaptor captor, Instant start, Instant end, MeasureStep step){
        this.checkReadMeasuresAgrs(captor, start, end, step);
        List<Measure> measures = new ArrayList<>();
        Instant current = start;
        while(current.isBefore(end)){
            measures.add(new Measure(current, bigCorpApplicationProperties.getMeasure().getDefaultReal(), captor));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measures;
    }
}