package com.discipline.thyme.dynamicdatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class DataBaseManager {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public DataBaseConfig getConfigByName(String name){
        List<DataBaseConfig> list = jdbcTemplate.query("",new MyRowMapper());
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }

    private class MyRowMapper implements RowMapper<DataBaseConfig>{

        @Override
        public DataBaseConfig mapRow(ResultSet resultSet, int i) throws SQLException {
            DataBaseConfig config = new DataBaseConfig();
            config.setId(resultSet.getString("id"));
            config.setName(resultSet.getString("name"));
            config.setUsername(resultSet.getString("username"));
            config.setPassword(resultSet.getString("password"));
            config.setUrl(resultSet.getString("url"));
            config.setType(resultSet.getString("type"));
            return config;
        }
    }
}
