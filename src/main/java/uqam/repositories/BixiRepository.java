package uqam.repositories;

import uqam.resources.Bixi;
import java.util.*;
import java.util.stream.*;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import uqam.rowMapper.BixiRowMapper;

@Component
public class BixiRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_STMT
            = " select"
            + "     id"
            + "   , station_name"
            + "   , station_id"
            + "   , station_state"
            + "   , station_is_blocked"
            + "   , station_under_maintenance"
            + "   , station_out_of_order"
            + "   , millis_last_update"
            + "   , millis_last_server_communication"
            + "   , bk"
            + "   , bl"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + "   , available_terminals"
            + "   , unavailable_terminals"
            + "   , available_bikes"
            + "   , unavailable_bikes"
            + " from"
            + "   bixies";

    public List<Bixi> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BixiRowMapper());
    }
    
    public List<Bixi> findByParams(String paramsStmt){
        return jdbcTemplate.query(FIND_ALL_STMT + paramsStmt, new BixiRowMapper());
    }

    private static final String FIND_BY_ID_STMT
            = " select"
            + "     id"
            + "   , station_name"
            + "   , station_id"
            + "   , station_state"
            + "   , station_is_blocked"
            + "   , station_under_maintenance"
            + "   , station_out_of_order"
            + "   , millis_last_update"
            + "   , millis_last_server_communication"
            + "   , bk"
            + "   , bl"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + "   , available_terminals"
            + "   , unavailable_terminals"
            + "   , available_bikes"
            + "   , unavailable_bikes"
            + " from"
            + "   bixies"
            + " where"
            + "   id = ?";

    public Bixi findById(int id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new BixiRowMapper());
    }

    private static final String FIND_BY_STATION_NAME_STMT
            = " select"
            + "     id"
            + "   , ts_headline(station_name, q, 'HighlightAll = true') as station_name"
            + "   , station_id"
            + "   , station_state"
            + "   , station_is_blocked"
            + "   , station_under_maintenance"
            + "   , station_out_of_order"
            + "   , millis_last_update"
            + "   , millis_last_server_communication"
            + "   , bk"
            + "   , bl"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + "   , available_terminals"
            + "   , unavailable_terminals"
            + "   , available_bikes"
            + "   , unavailable_bikes"
            + " from"
            + "     bixies"
            + "   , to_tsquery(?) as q"
            + " where"
            + "   station_name @@ q"
            + " order by"
            + "   ts_rank_cd(to_tsvector(station_name), q) desc";

    public List<Bixi> findByName(String... tsterms) {
        String tsquery = Arrays.stream(tsterms).collect(Collectors.joining(" & "));
        return jdbcTemplate.query(FIND_BY_STATION_NAME_STMT, new Object[]{tsquery}, new BixiRowMapper());
    }
    
    private static final String FIND_BY_RADIUS_STMT
            = " select"
            + "     id"
            + "   , station_name"
            + "   , station_id"
            + "   , station_state"
            + "   , station_is_blocked"
            + "   , station_under_maintenance"
            + "   , station_out_of_order"
            + "   , millis_last_update"
            + "   , millis_last_server_communication"
            + "   , bk"
            + "   , bl"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + "   , available_terminals"
            + "   , unavailable_terminals"
            + "   , available_bikes"
            + "   , unavailable_bikes"
            + " from"
            + "     bixies"
            + " where"
            + "     ST_DWithin(coordinates, ST_MakePoint(?, ?) , ?)";
    
    public List<Bixi> findByRadius() {
        return jdbcTemplate.query(FIND_BY_RADIUS_STMT, new BixiRowMapper());
    }
    
    private static final String INSERT_STMT
            = " INSERT INTO bixies"
            + " (id, "
            + " station_name, "
            + " station_id, "
            + " station_state, "
            + " station_is_blocked, "
            + " station_under_maintenance, "
            + " station_out_of_order,"
            + " millis_last_update, "
            + " millis_last_server_communication, "
            + " bk, "
            + " bl, "
            + " coordinates, "
            + " available_terminals, "
            + " unavailable_terminals, "
            + " available_bikes, "
            + " unavailable_bikes)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeographyFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')'), ?, ?, ?, ?)"
            + " on conflict do nothing";

    public int insert(Bixi bixi) throws Exception {
        // Message: Insertion table bixies avec succes
        int numRowsBix = insertBixi(bixi);
        System.out.println("TABLE BIXIES: " + 1 + " ROW(S) AFFECTED.");
        return numRowsBix;
    }

    public int insertBixi(Bixi bixi) throws Exception {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_STMT);
            ps.setInt(1, bixi.getId());
            ps.setString(2, bixi.getStationName());
            ps.setString(3, bixi.getStationId());
            ps.setInt(4, bixi.getStationState());
            ps.setBoolean(5, bixi.getStationIsBlocked());
            ps.setBoolean(6, bixi.getStationUnderMaintenance());
            ps.setBoolean(7, bixi.getStationOutOfOrder());
            ps.setLong(8, bixi.getMillisLastUpdate());
            ps.setLong(9, bixi.getMillisLastServerCommunication());
            ps.setBoolean(10, bixi.getBk());
            ps.setBoolean(11, bixi.getBl());
            ps.setDouble(12, bixi.getLat());
            ps.setDouble(13, bixi.getLng());
            ps.setInt(14, bixi.getAvailableTerminals());
            ps.setInt(15, bixi.getUnavailableTerminals());
            ps.setInt(16, bixi.getAvailableBikes());
            ps.setInt(17, bixi.getUnavailableBikes());
            return ps;
        });
    }

}
