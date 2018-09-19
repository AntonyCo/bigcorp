package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.controller.dto.MeasureWithCaptorDto;
import com.training.spring.bigcorp.exception.NotFoundException;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.MeasureServiceImpl;
import com.training.spring.bigcorp.service.SiteService;
import com.training.spring.bigcorp.utils.SseEmitterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/measures")
@Transactional
public class MeasureController {
    private final static Logger logger = LoggerFactory.getLogger(SiteService.class);
    private MeasureServiceImpl measureService;
    private CaptorDao captorDao;
    private SseEmitterUtils sseEmitterUtils;

    public MeasureController(MeasureServiceImpl measureService, CaptorDao captorDao, SseEmitterUtils sseEmitterUtils) {
        this.measureService = measureService;
        this.captorDao = captorDao;
        this.sseEmitterUtils = sseEmitterUtils;
    }
    @GetMapping
    public ModelAndView displayMeasures(){
        return new ModelAndView("measures")
                .addObject("captors",
                        captorDao.findAll()
                                .stream()
                                .sorted(Comparator.comparing(Captor::getName))
                                .map(c -> "{ id: '" + c.getId() + "', name: '" +
                                        c.getName() + "'}")
                                .collect(Collectors.joining(",")));
    }
    @GetMapping("captors/{id}/last/hours/{nbHours}")
    public List<MeasureDto> displaySites(@PathVariable String id, @PathVariable long nbHours){
        Captor captor = captorDao.findById(id).orElseThrow(NotFoundException::new);
        if (captor.getPowerSource() == PowerSource.SIMULATED) {
            return measureService.readMeasures(((SimulatedCaptor) captor),
                    Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),
                    MeasureStep.ONE_MINUTE)
                    .stream()
                    .map(m -> new MeasureDto(m.getInstant(),
                            m.getValueInWatt()))
                    .collect(Collectors.toList());
        }
        // Pour le moment on ne gère qu'un type
        return new ArrayList<>();
    }


    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events() {
        return sseEmitterUtils.createEmitter();
    }

    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    public void readMeasure() {
        captorDao
                .findAll()
                .stream()
                .map(captor -> {
                    Measure measure = measureService.readAndSaveMeasure(captor);
                    return new MeasureWithCaptorDto(captor, measure.getInstant(),
                            measure.getValueInWatt());
                })
                .forEach(this::sendEventForUser);
    }
    private void sendEventForUser(MeasureWithCaptorDto measure) {
        sseEmitterUtils.getEmitters().forEach(sseEmitter -> {
            try {
                sseEmitter.send(measure);
            } catch (IOException e) {
                logger.error("Error on event emit", e);
            }
        });
    }
}
