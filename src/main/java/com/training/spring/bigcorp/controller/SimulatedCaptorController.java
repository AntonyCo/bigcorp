package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.SimulatedCaptor;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
@RequestMapping("/sites/{siteId}/captors/SIMULATED")
public class SimulatedCaptorController {
    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private SiteDao siteDao;
    @Autowired
    private MeasureDao measureDao;

    @GetMapping
    public ModelAndView displaySites(Model model, @PathVariable String siteId){
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        return new ModelAndView("simulatedCaptor")
                .addObject("siteId", site.getId())
                .addObject("captors", captorDao.findBySiteId(site.getId()));
    }

    @GetMapping("/create")
    public ModelAndView create(Model model, @PathVariable String siteId){
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        return new ModelAndView("simulatedCaptor")
                .addObject("site", site)
                .addObject("captor", new SimulatedCaptor());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(@PathVariable String siteId, SimulatedCaptor captor) {
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        SimulatedCaptor captorToPersist;
        if (captor.getId() == null) {
            captorToPersist = new SimulatedCaptor(captor.getName(), site, captor.getMinPowerInWatt(), captor.getMaxPowerInWatt());
        } else {
            captorToPersist = (SimulatedCaptor) captorDao.findById(captor.getId())
                    .orElseThrow(IllegalArgumentException::new);
            captorToPersist.setName(captor.getName());
            captorToPersist.setMinPowerInWatt(captor.getMinPowerInWatt());
            captorToPersist.setMaxPowerInWatt(captor.getMaxPowerInWatt());
        }
        captorDao.save(captorToPersist);
        return new ModelAndView("site").addObject("site", site);
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String id) {
        measureDao.deleteByCaptorId(id);
        captorDao.deleteById(id);
        return new ModelAndView("sites").addObject("sites", siteDao.findAll());
    }
}
