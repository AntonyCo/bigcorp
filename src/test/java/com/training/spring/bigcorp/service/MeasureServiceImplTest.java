package com.training.spring.bigcorp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringJUnit4ClassRunner.class)
@RestClientTest(MeasureServiceImpl.class)
public class MeasureServiceImplTest {
    @TestConfiguration
    @ComponentScan("com.training.spring.bigcorp.service")
    public static class MeasureServiceImplTestConfig {
        public static MeasureDao measureDao = Mockito.mock(MeasureDao.class);
        @Bean
        public CaptorDao captorDao() {
            return Mockito.mock(CaptorDao.class);
        }
        @Bean
        public MeasureDao measureDao() {
            return measureDao;
        }
    }
    @Autowired
    private MeasureService service;
    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Captors used in tests
     */
    private SimulatedCaptor simulatedCaptor;
    private FixedCaptor fixedCaptor;
    private RealCaptor realCaptor;
    @Before
    public void init() {
        Site site = new Site("Florange");
        simulatedCaptor = new SimulatedCaptor("test", site, 500_000, 1_000_000);
        simulatedCaptor.setId("id1");
        fixedCaptor = new FixedCaptor("test", site, 500_000);
        fixedCaptor.setId("id2");
        realCaptor = new RealCaptor("test", site);
        realCaptor.setId("id3");
    }

    @Test
    public void readMeasures() {
        Instant start = Instant.parse("2018-09-01T21:00:00Z");
        Instant end = Instant.parse("2018-09-01T23:30:00Z");
        List<Measure> expectedMeasures = Arrays.asList(
                new Measure(Instant.parse("2018-09-01T21:00:00Z"), 1000, realCaptor),
                new Measure(Instant.parse("2018-09-01T21:15:00Z"), 2000, realCaptor),
                new Measure(Instant.parse("2018-09-01T23:30:00Z"), 4567, realCaptor));

        Mockito.when(MeasureServiceImplTestConfig.measureDao.findMeasureByIntervalAndCaptor(eq
                        (start), eq(end), anyString()))
                .thenReturn(expectedMeasures);
        List<Measure> measures = service.readMeasures(realCaptor, start, end,
                MeasureStep.ONE_HOUR);
        assertThat(measures).hasSize(3);
// And we have a value for each hour of the period
        assertThat(measures)
                .extracting("instant", "valueInWatt")
                .containsExactly(
                        tuple(Instant.parse("2018-09-01T21:00:00Z"), 1500),
                        tuple(Instant.parse("2018-09-01T22:00:00Z"), 0),
                        tuple(Instant.parse("2018-09-01T23:00:00Z"), 4567));
    }
    @Test
    public void readMeasuresThrowsExceptionWhenArgIsNull() {
        Instant start = Instant.parse("2018-09-01T22:00:00Z");
        Instant end = start.plusSeconds(60 * 60 * 24);
        assertThatThrownBy(() -> service.readMeasures(null, start, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("captor is required");
        assertThatThrownBy(() -> service.readMeasures(realCaptor, null, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start is required");
        assertThatThrownBy(() -> service.readMeasures(simulatedCaptor, start, null,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("end is required");
        assertThatThrownBy(() -> service.readMeasures(fixedCaptor, start, end, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("step is required");
        assertThatThrownBy(() -> service.readMeasures(realCaptor, end, start,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start must be before end");
    }

    @Test
    public void readAndSaveRealMeasure() throws Exception {
        MeasureDto expectedMeasure = new MeasureDto(Instant.parse("2018-09-01T21:00:00Z"),
                10_000_000);
        String expectedJson = objectMapper.writeValueAsString(expectedMeasure);
        String request =
                "http://localhost:8090/measures/one?lastValue=0&variance=1000000";
        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson,
                        MediaType.APPLICATION_JSON));
        Measure measure = service.readAndSaveMeasure(realCaptor);
        assertThat(measure.getCaptor()).isEqualTo(realCaptor);
        assertThat(measure.getValueInWatt()).isEqualTo(10_000_000);
    }
    @Test
    public void readAndSaveSimulatedMeasure() throws Exception {
        MeasureDto expectedMeasure = new MeasureDto(Instant.parse("2018-09-01T21:00:00Z"),
                10_000_000);
        String expectedJson = objectMapper.writeValueAsString(expectedMeasure);
        String request =
                "http://localhost:8090/measures/one?lastValue=0&variance=1000000";
        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson,
                        MediaType.APPLICATION_JSON));
        Measure measure = service.readAndSaveMeasure(simulatedCaptor);
        assertThat(measure.getCaptor()).isEqualTo(simulatedCaptor);
        assertThat(measure.getValueInWatt()).isEqualTo(10_000_000);
    }
    @Test
    public void readAndSaveFixedMeasure() {
        Measure measure = service.readAndSaveMeasure(fixedCaptor);
        assertThat(measure.getCaptor()).isEqualTo(fixedCaptor);
        assertThat(measure.getValueInWatt()).isEqualTo(500_000);
    }
}
