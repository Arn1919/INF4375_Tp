/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author arnaud
 */
public class ActivityDatesRowMapper implements RowMapper<Date> {

    @Override
    public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp temp = rs.getTimestamp("event_date");   
        return new Date(temp.getTime());
    }
}
