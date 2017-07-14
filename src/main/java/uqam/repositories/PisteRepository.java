package uqam.repositories;

import uqam.resources.Piste;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import uqam.resources.Point;

@Component
public class PisteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_STMT
            = " select"
            + "     id"
            + "   , type_voie1"
            + "   , type_voie2"
            + "   , longueur"
            + "   , nbr_voie"
            + "   , nom_arr_ville"
            + "   , coordinates1"
            + "   , coordinates2"
            + " from"
            + "   pistes";

    public List<Piste> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new PisteRowMapper());
    }

    private static final String FIND_BY_ID_STMT
            = " select"
            + "     id"
            + "   , type_voie1"
            + "   , type_voie2"
            + "   , longueur"
            + "   , nbr_voie"
            + "   , nom_arr_ville"
            + "   , coordinates1"
            + "   , coordinates2"
            + " from"
            + "   pistes"
            + " where"
            + "   id = ?";

    public Piste findById(int id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new PisteRowMapper());
    }

    private static final String INSERT_STMT
            = " INSERT INTO pistes"
            + " ("
            + " id, "
            + " type_voie1, "
            + " type_voie2, "
            + " longueur, "
            + " nbr_voie, "
            + " nom_arr_ville, "
            + " coordinates1,"
            + " coordinates2"
            + " ) "
            + " VALUES (?, ?, ?, ?, ?, ?, "
            + " ST_GeographyFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')'), "
            + " ST_GeographyFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')') "
            + " )"
            + " on conflict do nothing";

    public int insert(Piste piste) throws Exception {
        // Message: Insertion table bixies avec succes
        try{
            int numRowsPis = insertPiste(piste);
        }catch(BadSqlGrammarException e){
            System.out.println("HERE");
            System.out.println(e);
            System.out.println("HERE");
        }
        System.out.println("TABLE PISTES: " + 1 + " ROW(S) AFFECTED.");
        return 0;
    }

    public int insertPiste(Piste piste) throws Exception {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_STMT);
            ps.setInt(1, piste.getId());
            ps.setInt(2, piste.getTypeVoie1());
            ps.setInt(3, piste.getTypeVoie2());
            ps.setInt(4, piste.getLongueur());
            ps.setInt(5, piste.getNbreVoie());
            ps.setString(6, piste.getNomArrVille());
            ps.setDouble(7, piste.getCoordinates().get(0).getLng());
            ps.setDouble(8, piste.getCoordinates().get(0).getLat());
            ps.setDouble(9, piste.getCoordinates().get(1).getLng());
            ps.setDouble(10, piste.getCoordinates().get(1).getLat());
            return ps;
        });
    }

}

class PisteRowMapper implements RowMapper<Piste> {

    @Override
    public Piste mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<Point> coordinates = new ArrayList<>();
        
        return new Piste(
                rs.getInt("id"),
                rs.getInt("type_voie1"),
                rs.getInt("station_id"),
                rs.getInt("station_state"),
                rs.getInt("station_is_blocked"),
                rs.getString("station_under_maintenance"),
                coordinates
        );
    }
}
