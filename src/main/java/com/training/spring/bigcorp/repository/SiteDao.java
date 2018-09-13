package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Site;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteDao extends CrudDao<Site, String> {
}
