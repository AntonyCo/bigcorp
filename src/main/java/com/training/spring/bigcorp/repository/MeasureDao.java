package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureDao extends CrudDao<Measure, String> {
}
