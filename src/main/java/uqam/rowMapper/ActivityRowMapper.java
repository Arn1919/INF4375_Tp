/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uqam.repositories.ActivityRepository;
import uqam.resources.Activity;
import uqam.resources.Lieu;

@Service
public class ActivityRowMapper implements RowMapper<Activity> {

    @Autowired
    ActivityRepository activityRepository;

    @Override
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Date> dates = activityRepository.findAllDatesById(rs.getInt("id"));

        return new Activity(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("district"),
                dates,
                new Lieu(
                        rs.getString("venue_name"),
                        rs.getDouble("lat"),
                        rs.getDouble("lng")
                )
        );
    }
}
