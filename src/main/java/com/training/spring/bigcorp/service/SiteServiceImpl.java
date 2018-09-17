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
    private SiteDao siteDao;
    private CaptorService captorService;

    public SiteServiceImpl() {}

    @Autowired
    public SiteServiceImpl(SiteDao siteDao, CaptorService captorService) {
        logger.debug("Init SiteServiceImpl :{}", this);
        this.siteDao = siteDao;
        this.captorService = captorService;
    }
    @Override
    @Monitored
    public Site findById(String siteId) {
        logger.debug("Appel de findById :{}", this);
        if (siteId == null) {
            return null;
        }

        Optional<Site> Optsite = siteDao.findById(siteId);
        if(!Optsite.isPresent()){
            return null;
        }
        Site site = Optsite.get();
        //site.setCaptors(captorService.findBySite(siteId));
        return site;
    }
}
