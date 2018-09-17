package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
@RequestMapping("/sites/{siteId}/captors/FIXED")
public class FixedCaptorController {
    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private SiteDao siteDao;

    @GetMapping
    public ModelAndView displaySites(Model model, @PathVariable String siteId){
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        return new ModelAndView("fixedCaptor")
                .addObject("siteId", site.getId())
                .addObject("captors", captorDao.findBySiteId(site.getId()));
    }

    @GetMapping("/create")
    public ModelAndView create(Model model){
        return new ModelAndView("site").addObject("site", new Site());
    }


}
