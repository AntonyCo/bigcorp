package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MeasureDao extends JpaRepository<Measure, Long> {
    void deleteByCaptorId(String captorId);

    @Query("SELECT m FROM Measure m WHERE m.instant BETWEEN :start AND :end AND m.captor.id = :captorId")
    List<Measure> findMeasureByIntervalAndCaptor(Instant start, Instant end, String captorId);

    Measure findTopByCaptorIdOrderByInstantDesc(String captorId);
}