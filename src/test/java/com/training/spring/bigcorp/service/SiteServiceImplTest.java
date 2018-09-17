package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class SiteServiceImplTest {
    @Mock
    private SiteDao siteDao;
    @Mock
    private CaptorDao captorDao;
    @InjectMocks
    private SiteServiceImpl siteService;
    @InjectMocks
    private CaptorServiceImpl captorService;
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void findByIdShouldReturnNullWhenIdIsNull() {
        // Initialisation
        String siteId = null;
        // Appel du SUT
        Site site = siteService.findById(siteId);
        // Vérification
        Assertions.assertThat(site).isNull();
    }
    @Test
    public void findById() {
        // Initialisation
        String siteId = "siteId";
        Optional<Site> siteA = Optional.of(new Site("Site A"));
        FixedCaptor expectedCaptor = new FixedCaptor("Capteur A", new Site("Florange"), 0);
        Mockito.when(siteDao.findById(siteId)).thenReturn(siteA);
        Mockito.when(captorDao.findBySiteId(siteId)).thenReturn(Arrays.asList(expectedCaptor));

        // Appel du SUT
        Site site =  siteService.findById(siteId);
        Set<Captor> captors = captorService.findBySite(siteId);
        site.setCaptors(captors);

        // Vérification
        Assertions.assertThat(site)
                .extracting(Site::getName)
                .containsExactly("Site A");

        Assertions.assertThat(site)
                .extracting(Site::getCaptors)
                .hasSize(1);
    }

    @Test
    public void findByIdShouldReturnNullWhenIsUnknow(){
        // Initialisation
        String siteId = "blablablablablal";
        // Appel du SUT
        Site site = siteService.findById(siteId);
        // Vérification
        Assertions.assertThat(site).isNull();
    }
}