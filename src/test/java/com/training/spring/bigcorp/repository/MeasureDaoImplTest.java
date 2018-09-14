package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.utils.H2DateConverter;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@JdbcTest
@ContextConfiguration(classes = {DaoTestConfig.class})
public class MeasureDaoImplTest {
    @Autowired
    private MeasureDao measureDao;
    private Captor captor;
    private H2DateConverter h2DateConverter;
    @Before
    public void init(){
        captor = new Captor("Laminoire à chaud", null, null);
        captor.setId("c2");
        h2DateConverter = new H2DateConverter();
    }

    @Test
    public void findById() {
        Measure measure = measureDao.findById(1L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1000000);

        measure = measureDao.findById(5L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1009678);
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Measure measure = measureDao.findById(40L);
        Assertions.assertThat(measure).isNull();
    }
    @Test
    public void findAll() {
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures)
                .hasSize(10)
                .extracting("id", "instant", "valueInWatt")
                .contains(Tuple.tuple(1L, h2DateConverter.convert("2018-08-09 13:00:00+02"),1000000))
                .contains(Tuple.tuple(2L,h2DateConverter.convert("2018-08-09 13:01:00+02"),1000124))
                .contains(Tuple.tuple(3L,h2DateConverter.convert("2018-08-09 13:02:00+02"),1001234))
                .contains(Tuple.tuple(4L,h2DateConverter.convert("2018-08-09 13:03:00+02"),1001236))
                .contains(Tuple.tuple(5L,h2DateConverter.convert("2018-08-09 13:04:00+02"),1009678))
                .contains(Tuple.tuple(6L,h2DateConverter.convert("2018-08-09 13:00:00+02"),-9000000))
                .contains(Tuple.tuple(7L,h2DateConverter.convert("2018-08-09 13:01:00+02"),-900124))
                .contains(Tuple.tuple(8L,h2DateConverter.convert("2018-08-09 13:02:00+02"),-901234))
                .contains(Tuple.tuple(9L,h2DateConverter.convert("2018-08-09 13:03:00+02"),-901236))
                .contains(Tuple.tuple(10L,h2DateConverter.convert("2018-08-09 13:04:00+02"),-909678)
                );
    }
    @Test
    public void create() {
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.create(new Measure(Instant.now(), 1000000, captor));
        Assertions.assertThat(measureDao.findAll())
                .hasSize(11)
                .extracting(Measure::getId)
                .contains(
                        Long.valueOf(1),
                        Long.valueOf(2),
                        Long.valueOf(3),
                        Long.valueOf(4),
                        Long.valueOf(5),
                        Long.valueOf(6),
                        Long.valueOf(7),
                        Long.valueOf(8),
                        Long.valueOf(9),
                        Long.valueOf(10),
                        Long.valueOf(11)
                );
    }
    @Test
    public void update() {
        Measure measure = measureDao.findById(3L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1001234);
        measure.setValueInWatt(1005555);
        measureDao.update(measure);
        measure = measureDao.findById(3L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1005555);
    }
    @Test
    public void deleteById() {
        Measure newmeasure = new Measure(Instant.now(), 1005555, captor);
        newmeasure.setId(11L);
        measureDao.create(newmeasure);
        Assertions.assertThat(measureDao.findById(newmeasure.getId())).isNotNull();
        measureDao.deleteById(newmeasure.getId());
        Assertions.assertThat(measureDao.findById(newmeasure.getId())).isNull();
    }
}