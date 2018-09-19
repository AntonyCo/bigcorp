package com.training.spring.bigcorp.controller.dto;

import com.training.spring.bigcorp.model.Captor;

import java.time.Instant;

public class MeasureWithCaptorDto {
    private final String captorId;
    private final String captorName;
    private final String siteId;
    private final String siteName;
    private final Instant instant;
    private final Integer valueInWatt;
    public MeasureWithCaptorDto(Captor captor,
                                Instant instant,
                                Integer valueInWatt) {
        this.captorId = captor.getId();
        this.captorName = captor.getName();
        this.siteId = captor.getSite().getId();
        this.siteName = captor.getSite().getName();
        this.instant = instant;
        this.valueInWatt = valueInWatt;
    }

    public String getCaptorId() {
        return captorId;
    }

    public String getCaptorName() {
        return captorName;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public Instant getInstant() {
        return instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }
}