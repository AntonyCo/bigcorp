package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {
    @Autowired
    private CaptorDao captorDao;
    @Autowired
    private EntityManager entityManager;
    private Site site;

    @Before
    public void init(){
        site = new Site("name");
        site.setId("site1");
    }
    @Test
    public void findById() {
        Optional<Captor> captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting(Captor::getName)
                .containsExactly("Eolienne");
    }
    @Test
    public void findByIdShouldReturnNullWhenIdUnknown() {
        Optional<Captor> captor = captorDao.findById("unknown");
        Assertions.assertThat(captor).isEmpty();
    }
    @Test
    public void findAll() {
        List<Captor> captors = captorDao.findAll();
        Assertions.assertThat(captors)
                .hasSize(2)
                .extracting("id", "name")
                .contains(Tuple.tuple("c1", "Eolienne"))
                .contains(Tuple.tuple("c2", "Laminoire à chaud"));
    }
    @Test
    public void create() {
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        captorDao.save(new RealCaptor("New captor", site));
        Assertions.assertThat(captorDao.findAll())
                .hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne", "Laminoire à chaud", "New captor");
    }
    @Test
    public void update() {
        Optional<Captor> captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting(Captor::getName)
                .containsExactly("Eolienne");
        captor.ifPresent(c->{
            c.setName("Captor updated");
            captorDao.save(c);
        });
        captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting(Captor::getName)
                .containsExactly("Captor updated");
    }
    @Test
    public void deleteById() {
        Captor newcaptor = new RealCaptor("New captor", site);
        captorDao.save(newcaptor);
        Assertions.assertThat(captorDao.findById(newcaptor.getId())).isNotEmpty();
        captorDao.delete(newcaptor);
        Assertions.assertThat(captorDao.findById(newcaptor.getId())).isEmpty();
    }
    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey() {
        Captor captor = captorDao.getOne("c1");
        Assertions
                .assertThatThrownBy(() -> {
                    captorDao.delete(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void findByExample() {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", m -> m.ignoreCase().contains())
                .withIgnorePaths("id")
                .withIgnoreNullValues();
        Captor captor = new FixedCaptor("eolienne", null);
        captor.setId("c1");
        List<Captor> captors = captorDao.findAll(Example.of(captor, matcher));
        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1", "Eolienne"));
    }
}