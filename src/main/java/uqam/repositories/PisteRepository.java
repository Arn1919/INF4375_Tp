package uqam.repositories;

import uqam.resources.Piste;
import uqam.rowMapper.PisteRowMapper;
import java.util.*;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

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
            + "   , ST_X(coordinates1::geometry) AS lat1"
            + "   , ST_Y(coordinates1::geometry) AS lng1"
            + "   , ST_X(coordinates2::geometry) AS lat2"
            + "   , ST_Y(coordinates2::geometry) AS lng2"
            + " from"
            + "   pistes";

    public List<Piste> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new PisteRowMapper());
    }

    private static final String FIND_BY_PARAMS_STMT
            = " SELECT"
            + "     id"
            + "   , type_voie1"
            + "   , type_voie2"
            + "   , longueur"
            + "   , nbr_voie"
            + "   , nom_arr_ville"
            // SOMETHING ABOUT LINESTRING
            + " FROM"
            + "     pistes";

    public List<Piste> findByParams(String paramsStmt) {
        return jdbcTemplate.query(FIND_BY_PARAMS_STMT + paramsStmt, new PisteRowMapper());
    }

    private static final String FIND_BY_ID_STMT
            = " select"
            + "     id"
            + "   , type_voie1"
            + "   , type_voie2"
            + "   , longueur"
            + "   , nbr_voie"
            + "   , nom_arr_ville"
            + "   , ST_X(coordinates1::geometry) AS lat1"
            + "   , ST_Y(coordinates1::geometry) AS lng1"
            + "   , ST_X(coordinates2::geometry) AS lat2"
            + "   , ST_Y(coordinates2::geometry) AS lng2"
            + " from"
            + "   pistes"
            + " where"
            + "   id = ?";

    public Piste findById(int id) {
        try {
            Piste test = jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new PisteRowMapper());
        } catch (Exception e) {
            System.out.println(e);
        }
        return jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new PisteRowMapper());
    }

    private static final String INSERT_PISTES_STMT
            = " INSERT INTO pistes("
            + "     id, "
            + "     type_voie1, "
            + "     type_voie2, "
            + "     longueur, "
            + "     nbr_voie, "
            + "     nom_arr_ville, "
            + "     piste"
            + " ) "
            + " VALUES (?, ?, ?, ?, ?, ?, ?::geography) "
            + " on conflict do nothing";

    public void insert(Piste piste) throws Exception {
        int numRowsPis = insertPiste(piste);
        // Message: Insertion table pistes avec succes
        System.out.println("TABLE PISTES: " + 1 + " ROW(S) AFFECTED.");
    }

    public int insertPiste(Piste piste) throws Exception {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_PISTES_STMT);
            ps.setInt(1, piste.getId());
            ps.setInt(2, piste.getTypeVoie1());
            ps.setInt(3, piste.getTypeVoie2());
            ps.setInt(4, piste.getLongueur());
            ps.setInt(5, piste.getNbreVoie());
            ps.setString(6, piste.getNomArrVille());
            ps.setString(7, piste.getMultiLineString());
            return ps;
        });
    }

}


