package com.training.spring.bigcorp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bigcorp")
public class BigCorpApplicationMeasureProperties {
    private Integer defaultFixed;
    private Integer defaultReal;
    private Integer defaultSimulated;

    public Integer getDefaultFixed() {
        return defaultFixed;
    }

    public void setDefaultFixed(Integer defaultFixed) {
        this.defaultFixed = defaultFixed;
    }

    public Integer getDefaultReal() {
        return defaultReal;
    }

    public void setDefaultReal(Integer defaultReal) {
        this.defaultReal = defaultReal;
    }

    public Integer getDefaultSimulated() {
        return defaultSimulated;
    }

    public void setDefaultSimulated(Integer defaultSimulated) {
        this.defaultSimulated = defaultSimulated;
    }
}
