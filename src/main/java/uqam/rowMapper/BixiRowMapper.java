/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uqam.resources.Bixi;

/**
 *
 * @author arnaud
 */
public class BixiRowMapper implements RowMapper<Bixi> {

    @Override
    public Bixi mapRow(ResultSet rs, int rowNum) throws SQLException {        
        
        return new Bixi(
                rs.getInt("id"),
                rs.getString("station_name"),
                rs.getString("station_id"),
                rs.getInt("station_state"),
                rs.getBoolean("station_is_blocked"),
                rs.getBoolean("station_under_maintenance"),
                rs.getBoolean("station_out_of_order"),
                rs.getLong("millis_last_update"),
                rs.getLong("millis_last_server_communication"),
                rs.getBoolean("bk"),
                rs.getBoolean("bl"),
                rs.getDouble("lat"),
                rs.getDouble("lng"),
                rs.getInt("available_terminals"),
                rs.getInt("unavailable_terminals"),
                rs.getInt("available_bikes"),
                rs.getInt("unavailable_bikes")
        );
    }
}
