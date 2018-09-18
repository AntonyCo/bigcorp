package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Transactional
@RequestMapping("/sites")
public class SiteController {
    @Autowired
    private SiteDao siteDao;
    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private MeasureDao measureDao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displaySites(Model model){
        return new ModelAndView("sites").addObject("sites", siteDao.findAll());
    }
    @GetMapping("/create/{id}")
    public ModelAndView findById(@PathVariable String id) {
        return new ModelAndView("site")
                .addObject("site",
                        siteDao.findById(id).orElseThrow(IllegalArgumentException::new));
    }
    @GetMapping("/create")
    public ModelAndView create(Model model){
        return new ModelAndView("site").addObject("site", new Site());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(Site site) {
        if (site.getId() == null) {
            return new ModelAndView("site").addObject("site", siteDao.save(site));

        } else {
            Site siteToPersist =
                    siteDao.findById(site.getId()).orElseThrow(IllegalArgumentException::new);
// L'utilisateur ne peut changer que le nom du site sur l'écran
            siteToPersist.setName(site.getName());
            return new ModelAndView("sites").addObject("sites", siteDao.findAll());
        }
    }
    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String id) {
        // Comme les capteurs sont liés à un site et les mesures sont liées à un capteur, nous devons faire
        // le ménage avant pour ne pas avoir d'erreur à la suppression d'un site utilisé ailleurs dans la base
        Site site = siteDao.findById(id).orElseThrow(IllegalArgumentException::new);
        site.getCaptors().forEach(c -> measureDao.deleteByCaptorId(c.getId()));
        captorDao.deleteBySiteId(id);
        siteDao.delete(site);
        return new ModelAndView("sites").addObject("sites", siteDao.findAll());
    }

   /* @GetMapping("/{id}/captors/{captorId}")
    public ModelAndView displayEditCaptor(@PathVariable String id, @PathVariable String captorId){
        Site site = siteDao.findById(id).orElseThrow(IllegalArgumentException::new);
        Captor cap = captorDao.findById(captorId).orElseThrow(IllegalArgumentException::new);
        if(cap.getPowerSource() == PowerSource.FIXED) {
            return new ModelAndView("fixedCaptor")
                    .addObject("site", site)
                    .addObject("captor", cap);
        }else if(cap.getPowerSource() == PowerSource.SIMULATED){
            return new ModelAndView("simulatedCaptor")
                    .addObject("site", site)
                    .addObject("captor", cap);
        }else{
            return new ModelAndView("site")
                    .addObject("site", site);
        }
    }*/
}
