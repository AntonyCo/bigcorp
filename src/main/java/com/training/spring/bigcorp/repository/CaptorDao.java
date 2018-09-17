package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaptorDao extends JpaRepository<Captor, String> {
    List<Captor> findBySiteId(String siteId);
    void deleteBySiteId(String siteId);
}
