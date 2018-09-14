package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.utils.H2DateConverter;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class MeasureDaoImplTest {
    @Autowired
    private MeasureDao measureDao;
    @Autowired
    private EntityManager entityManager;

    private Captor captor;
    private H2DateConverter h2DateConverter;
    @Before
    public void init(){
        captor = new RealCaptor("Laminoire à chaud", null);
        captor.setId("c2");
        h2DateConverter = new H2DateConverter();
    }

    @Test
    public void findById() {
        Optional<Measure> measure = measureDao.findById(-1L);
        Assertions.assertThat(measure)
                .get()
                .extracting(Measure::getValueInWatt)
                .containsExactly(1000000);

        measure = measureDao.findById(-5L);
        Assertions.assertThat(measure)
                .get()
                .extracting(Measure::getValueInWatt)
                .containsExactly(1009678);
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Measure> measure = measureDao.findById(40L);
        Assertions.assertThat(measure).isEmpty();
    }
    @Test
    public void findAll() {
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures)
                .hasSize(10)
                .extracting("id", "instant", "valueInWatt")
                .contains(Tuple.tuple(-1L, h2DateConverter.convert("2018-08-09 13:00:00+02"),1000000))
                .contains(Tuple.tuple(-2L,h2DateConverter.convert("2018-08-09 13:01:00+02"),1000124))
                .contains(Tuple.tuple(-3L,h2DateConverter.convert("2018-08-09 13:02:00+02"),1001234))
                .contains(Tuple.tuple(-4L,h2DateConverter.convert("2018-08-09 13:03:00+02"),1001236))
                .contains(Tuple.tuple(-5L,h2DateConverter.convert("2018-08-09 13:04:00+02"),1009678))
                .contains(Tuple.tuple(-6L,h2DateConverter.convert("2018-08-09 13:00:00+02"),-9000000))
                .contains(Tuple.tuple(-7L,h2DateConverter.convert("2018-08-09 13:01:00+02"),-900124))
                .contains(Tuple.tuple(-8L,h2DateConverter.convert("2018-08-09 13:02:00+02"),-901234))
                .contains(Tuple.tuple(-9L,h2DateConverter.convert("2018-08-09 13:03:00+02"),-901236))
                .contains(Tuple.tuple(-10L,h2DateConverter.convert("2018-08-09 13:04:00+02"),-909678)
                );
    }
    @Test
    public void create() {
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.save(new Measure(Instant.now(), 1000000, captor));
        Assertions.assertThat(measureDao.findAll())
                .hasSize(11)
                .extracting(Measure::getId)
                .contains(
                        1L,
                        -1L,
                        -2L,
                        -3L,
                        -4L,
                        -5L,
                        -6L,
                        -7L,
                        -8L,
                        -9L,
                        -10L
                );
    }
    @Test
    public void update() {
        Optional<Measure> measure = measureDao.findById(-3L);
        Assertions.assertThat(measure)
                .get()
                .extracting(Measure::getValueInWatt)
                .containsExactly(1001234);
        measure.ifPresent(m ->{
            m.setValueInWatt(1005555);
            measureDao.save(m);
        });
        measure = measureDao.findById(-3L);
        Assertions.assertThat(measure)
                .get()
                .extracting(Measure::getValueInWatt)
                .containsExactly(1005555);
    }
    @Test
    public void deleteById() {
        Measure newmeasure = new Measure(Instant.now(), 1005555, captor);
        measureDao.save(newmeasure);
        Assertions.assertThat(measureDao.findById(newmeasure.getId())).isNotEmpty();
        measureDao.delete(newmeasure);
        Assertions.assertThat(measureDao.findById(newmeasure.getId())).isEmpty();
    }
}