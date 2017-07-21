package uqam.repositories;

import uqam.resources.Piste;
import uqam.tasks.PisteRowMapper;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import uqam.resources.Ligne;
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
            = " INSERT INTO pistes"
            + " ("
            + " id, "
            + " type_voie1, "
            + " type_voie2, "
            + " longueur, "
            + " nbr_voie, "
            + " nom_arr_ville, "
            + " ) "
            + " VALUES (?, ?, ?, ?, ?, ?) "
            + " on conflict do nothing";

    public int insert(Piste piste) throws Exception {
        int numRowsPis = insertPiste(piste);
        // Message: Insertion table pistes avec succes
        System.out.println("TABLE PISTES: " + 1 + " ROW(S) AFFECTED.");
        for (Ligne ligne : piste.getLignes()) {
            int numRowsLig = insertLigne(piste, ligne.getPoints());
        }
        return 0;
    }

    public int insertPiste(Piste piste) throws Exception {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_PISTES_STMT);
            ps.setInt(1, piste.getId());
            ps.setInt(2, piste.getTypeVoie1());
            ps.setInt(3, piste.getTypeVoie2());
            ps.setInt(4, piste.getLongueur());
            ps.setInt(5, piste.getNbreVoie());
            ps.setObject(6, piste.getNomArrVille());
            return ps;
        });
    }

    private static final String INSERT_LIGNES_STMT
            = " INSERT INTO pistes_lignes"
            + " ("
            + " piste_id"
            + " ligne, "
            + " )"
            + " VALUES ("
            + " ?"
            + " , LINESTRING("
            + "   ? ? ?"
            + "   ,"
            + "   ? ? ?"
            + "   )"
            + " ) "
            + " on conflict (piste_id, ligne) do nothing";

    public int insertLigne(Piste piste, List<Point> points) throws Exception {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_LIGNES_STMT);
            ps.setInt(1, piste.getId());
            //ps.setInt(2, piste.getTypeVoie1());
            return ps;
        });
    }

}


