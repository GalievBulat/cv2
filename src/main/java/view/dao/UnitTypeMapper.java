package view.dao;

import view.interfaces.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnitTypeMapper implements RowMapper<UnitType> {
    @Override
    public UnitType getInstance(ResultSet resultSet) {
        try {
            return new UnitType.Builder().id(resultSet.getInt("id")).name(resultSet.getString("name")).create();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

}
