package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CaptorDaoImpl implements  CaptorDao{
    private static String SELECT_WITH_JOIN ="SELECT c.id, c.name, c.site_id, s.name as site_name " + "FROM Captor c inner join Site s on c.site_id = s.id ";
    private NamedParameterJdbcTemplate jdbcTemplate;

    public CaptorDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Captor> findBySiteId(String siteId) {
        return jdbcTemplate.query("SELECT c.id, c.name, c.site_id FROM Captor c inner join Site s on c.site_id = s.id where c.site_id = :site_id",
                new MapSqlParameterSource("site_id", siteId),
                this::captorMapper);

    }

    @Override
    public void create(Captor captor) {
        jdbcTemplate.update("insert into CAPTOR (id, name, site_id) values (:id, :name, :site_id)",new MapSqlParameterSource()
                .addValue("id", captor.getId())
                .addValue("name", captor.getName())
                .addValue("site_id", captor.getSite().getId()));
    }

    @Override
    public Captor findById(String id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_WITH_JOIN+"where c.id = :id",
                    new MapSqlParameterSource("id", id),
                    this::captorMapper);
        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    @Override
    public List<Captor> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOIN, this::captorMapper);
    }

    @Override
    public void update(Captor captor) {
        jdbcTemplate.update("update CAPTOR set name = :name, site_id = :site_id where id =:id",
                new MapSqlParameterSource()
                        .addValue("id", captor.getId())
                        .addValue("name", captor.getName())
                        .addValue("site_id", captor.getSite().getId()));
    }

    @Override
    public void deleteById(String id) {
        jdbcTemplate.update("delete from CAPTOR where id =:id",
                new MapSqlParameterSource()
                        .addValue("id", id));
    }

    private Captor captorMapper(ResultSet rs, int rowNum) throws SQLException {
        Site site = new Site(rs.getString("site_name"));
        site.setId(rs.getString("site_id"));
        Captor captor = new Captor(rs.getString("name"), null, site);
        captor.setId(rs.getString("id"));
        return captor;
    }
}
