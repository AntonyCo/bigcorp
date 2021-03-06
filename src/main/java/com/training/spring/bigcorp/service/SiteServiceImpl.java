package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.SiteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SiteServiceImpl implements SiteService {
    private final static Logger logger = LoggerFactory.getLogger(SiteService.class);
    private CaptorService captorService;

    public SiteServiceImpl() {}

    @Autowired
    public SiteServiceImpl(CaptorService captorService) {
        logger.debug("Init SiteServiceImpl :{}", this);
        this.captorService = captorService;
    }
    @Override
    @Monitored
    public Site findById(String siteId) {
        logger.debug("Appel de findById :{}", this);

        if(siteId == null){
            return null;
        }
        Site site = new Site("Florange");
        site.setId(siteId);
        site.setCaptors(captorService.findBySite(siteId));

        return site;
    }
}
