package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class MeasureServiceImpl implements MeasureService {

    private MeasureDao measureDao;
    private RestTemplate restTemplate;

    public MeasureServiceImpl(MeasureDao measureDao, RestTemplateBuilder builder) {
        this.measureDao = measureDao;
        this.restTemplate = builder.setConnectTimeout(1000).build();
    }
    @Override
    public List<Measure> readMeasures(Captor captor, Instant start, Instant end, MeasureStep step){
        checkReadMeasuresAgrs(captor, start, end, step);
        Set<MeasureByInterval> measureByIntervals = computeIntervals(start, end, step);
        List<Measure> measures = measureDao.findMeasureByIntervalAndCaptor(start, end, captor.getId());

        // A coder : Vous devez ici ajouter chacune des mesures retournées par le DAO dans le Set `measureByIntervals`
        measures.forEach(m -> {
            measureByIntervals.stream()
                    .filter(mbi -> mbi.contains(m.getInstant()))
                        .findAny()
                        .ifPresent(measureByInterval -> measureByInterval.power.add(m.getValueInWatt()));
        });
        // A coder : parcourir la liste des intervals et les transformer en `Measure`
        return measureByIntervals.stream()
                .map(measureByInterval -> {
                    Measure measure = new Measure(measureByInterval.start, measureByInterval.average(), captor);
                    return measure;
                })
                .sorted(Comparator.comparing(Measure::getInstant))
                .collect(Collectors.toList());

    }
    private Set<MeasureByInterval> computeIntervals(Instant start, Instant end,
                                                    MeasureStep step) {
        Set<MeasureByInterval> measureByIntervals = new HashSet<>();
        Instant current = start;
        Instant endInstant = end.isBefore(
                start.plusSeconds(step.getDurationInSecondes()))?
                            start.plusSeconds(step.getDurationInSecondes())
                        :
                            end;
        while (current.isBefore(endInstant)) {
            measureByIntervals.add(new MeasureByInterval(current,
                    current.plusSeconds(step.getDurationInSecondes())));
            current = current.plusSeconds(step.getDurationInSecondes());
        }
        return measureByIntervals;
    }

    class MeasureByInterval {
        private Instant start;
        private Instant end;
        private Set<Integer> power = new HashSet<>();
        public MeasureByInterval(Instant start, Instant end) {
            this.start = start;
            this.end = end;
        }
        public boolean contains(Instant instant) {
            return (instant.equals(start) || instant.isAfter(start)) &&
                    instant.isBefore(end);
        }

        public int average() {
            if (power.isEmpty()) {
                return 0;
            }
            return power
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.averagingInt(t -> t))
                    .intValue();
        }
    }
    @Override
    public Measure readAndSaveMeasure(Captor captor) {
        Measure measure = null;
        // A completer plus tard dans le TP. Pour le moment on garde la valeur nulle
        if(captor instanceof FixedCaptor){
            measure = new Measure(Instant.now(), ((FixedCaptor) captor).getDefaultPowerInWatt(), captor);
            measureDao.save(measure);
            return measure;
        }else{
            Integer lastValue = measureDao.findTopByCaptorIdOrderByInstantDesc(captor.getId()) != null ?
                    measureDao.findTopByCaptorIdOrderByInstantDesc(captor.getId()).getValueInWatt()
                    :
                    0;
            Integer variance = lastValue != 0 ? (lastValue * 10 / 100) : 1_000_000;
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8090/measures/one")
                    .path("")
                    .queryParam("lastValue", lastValue)
                    .queryParam("variance", variance);
            measure = this.restTemplate.getForObject(builder.toUriString(), Measure.class);
            measure.setCaptor(captor);
            measure.setInstant(Instant.now());
            measureDao.save(measure);
        }
        return measure;
    }
}