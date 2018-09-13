package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;

import java.time.Instant;
import java.util.List;

public interface MeasureService {
    List<Measure> readMeasures(Captor captor, Instant start, Instant end, MeasureStep step);

    default void checkReadMeasuresAgrs(Captor captor, Instant start, Instant end, MeasureStep step){
        if (captor == null) {
            throw new IllegalArgumentException("captor is required");
        }
        if (start == null) {
            throw new IllegalArgumentException("start is required");
        }
        if (end == null) {
            throw new IllegalArgumentException("end is required");
        }
        if (step == null) {
            throw new IllegalArgumentException("step is required");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must be before end");
        }
    }
}
