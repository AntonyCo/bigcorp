package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationMeasureProperties;
import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FixedMeasureService implements MeasureService<FixedCaptor> {

    @Autowired
    private BigCorpApplicationProperties bigCorpApplicationProperties;

    @Override public List<Measure> readMeasures(FixedCaptor captor, Instant start, Instant end, MeasureStep step){
        this.checkReadMeasuresAgrs(captor, start, end, step);
        List<Measure> measures = new ArrayList<>();
        Instant current = start;
        while(current.isBefore(end)){
            measures.add(new Measure(current, bigCorpApplicationProperties.getMeasure().getDefaultFixed(), captor));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measures;
    }
}

