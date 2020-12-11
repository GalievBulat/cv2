package view.dao;

import view.interfaces.RowMapper;
import view.models.Unit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnitMapper implements RowMapper<Unit> {
    @Override
    public Unit getInstance(ResultSet resultSet) {
        try {
            return new Unit.Builder().id(resultSet.getInt("id")).name(resultSet.getString("name")).create();
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

}
