/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uqam.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uqam.resources.Piste;

/**
 *
 * @author arnaud
 */
public class PisteRowMapper implements RowMapper<Piste> {

    @Override
    public Piste mapRow(ResultSet rs, int rowNum) throws SQLException {
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
