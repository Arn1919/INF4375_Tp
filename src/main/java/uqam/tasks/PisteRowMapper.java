/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import uqam.resources.Piste;
import uqam.resources.Point;

/**
 *
 * @author arnaud
 */
public class PisteRowMapper implements RowMapper<Piste> {

    @Override
    public Piste mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Point> coordinates = new ArrayList<>();
        coordinates.add(new Point(rs.getDouble("lat1"), rs.getDouble("lng1")));
        coordinates.add(new Point(rs.getDouble("lat2"), rs.getDouble("lng2")));
        return new Piste(
                rs.getInt("id"),
                rs.getInt("type_voie1"),
                rs.getInt("type_voie2"),
                rs.getInt("longueur"),
                rs.getInt("nbr_voie"),
                rs.getString("nom_arr_ville"),
                null
        );
    }
}
