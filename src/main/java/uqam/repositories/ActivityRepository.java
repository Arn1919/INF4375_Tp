package uqam.repositories;

import uqam.resources.Activity;
import uqam.rowMapper.ActivityRowMapper;
import uqam.rowMapper.ActivityDatesRowMapper;
import java.util.*;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import uqam.rowMapper.IntRowMapper;

@Component
public class ActivityRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ActivityRowMapper mapper;
    
    private static final String FIND_ACTIVITY_BY_PARAMS_STMT
            = " select"
            + "     activities.id"
            + "   , name"
            + "   , description"
            + "   , district"
            + "   , venue_name"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + " from"
            + "   activities"
            + " inner join"
            + "   activities_date"
            + " on"
            + "   activities_date.id = activities.id";
    
    public List<Activity> findByParams(String paramsStmt) {
        return jdbcTemplate.query(FIND_ACTIVITY_BY_PARAMS_STMT + paramsStmt, mapper);
    }
    
    private static final String FIND_ACTIVITY_ALL_STMT
            = " select"
            + "     activities.id"
            + "   , name"
            + "   , description"
            + "   , district"
            + "   , venue_name"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + " from"
            + "   activities";

    public List<Activity> findAll() {
        return jdbcTemplate.query(FIND_ACTIVITY_ALL_STMT, mapper);
    }

    private static final String FIND_ACTIVITY_BY_ID_STMT
            = " select"
            + "     activities.id"
            + "   , name"
            + "   , description"
            + "   , district"
            + "   , venue_name"
            + "   , ST_X(coordinates::geometry) AS lat"
            + "   , ST_Y(coordinates::geometry) AS lng"
            + " from"
            + "   activities"
            + " where"
            + "   activities.id = ?";

    public Activity findById(int id) {
        return jdbcTemplate.queryForObject(FIND_ACTIVITY_BY_ID_STMT, new Object[]{id}, mapper);
    }

    private static final String FIND_ALL_DATES_BY_ID_STMT
            = " select"
            + "     id"
            + "   , event_date"
            + "   , event_id"
            + " from"
            + "   activities_date";

    public List<Date> findAllDatesById(int id) {
        String complete_stmt = FIND_ALL_DATES_BY_ID_STMT + " where event_id = " + id;
        return jdbcTemplate.query(complete_stmt, new ActivityDatesRowMapper());
    }
    
    
    private static final String DELETE_ACTIVITY_DATE_STMT
            = " DELETE FROM activities_date"
            + " WHERE event_id = ?";
    
    private static final String DELETE_ACTIVITY_STMT
            = " DELETE FROM activities"
            + " WHERE id = ?";
    /**
     * Demande la suppression d'activite dans les tables activities et activities_date
     * 
     * @param id
     * @return int
     */
    public int delete(int id){
        int numRowsDat = deleteDate(id);
        int numRowsAct = deleteActivity(id);
        return numRowsAct + numRowsDat;
    }
    
    public int deleteActivity(int id){
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(DELETE_ACTIVITY_STMT);
            ps.setInt(1, id);
            return ps;
        });
    }
    
    public int deleteDate(int id){
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(DELETE_ACTIVITY_DATE_STMT);
            ps.setInt(1, id);
            return ps;
        });
    }
    
    private static final String PUT_ACTIVITY_STMT
            = " INSERT INTO activities (id, name, description, district, venue_name, coordinates)"
            + " VALUES( ?, ?, ?, ?, ?, ST_MakePoint(?, ?))"
            + " ON CONFLICT (id) DO UPDATE"
            + " SET"
            + "     name = ?,"
            + "     description = ?,"
            + "     district = ?,"
            + "     venue_name = ?,"
            + "     coordinates = ST_MakePoint(?, ?)";
    
    /**
     * Valide si l'objet avec le id en parametre existe deja dans la BD
     *  - Si non, il insere l'activite dans la BD a la position id
     *  - Si oui, il remplace l'activite a la position id par celle recut en parametre
     * 
     * @param id
     * @param activity
     * @throws Exception 
     */
    public void put(int id, Activity activity) throws Exception {
            activity.setId(id);
            int resultUpdate = update(activity);
            int resultDelete = deleteDate(id);
            int resultInsertDate = 0;
        for (int i = 0; i < activity.getDates().size(); i++) {
            resultInsertDate = insertDate(activity, i);
        }
    }
    
    /**
     * Met a jour l'activite a la position id par l'activite en parametre
     * 
     * @param activity
     * @return int
     */
    public int update(Activity activity) {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(PUT_ACTIVITY_STMT);
            ps.setInt(1, activity.getId());
            ps.setString(2, activity.getNom());
            ps.setString(3, activity.getDescription());
            ps.setString(4, activity.getArrondissement());
            ps.setString(5, activity.getLieu().getNom());
            ps.setDouble(6, activity.getLieu().getLng());
            ps.setDouble(7, activity.getLieu().getLat());
            ps.setString(8, activity.getNom());
            ps.setString(9, activity.getDescription());
            ps.setString(10, activity.getArrondissement());
            ps.setString(11, activity.getLieu().getNom());
            ps.setDouble(12, activity.getLieu().getLng());
            ps.setDouble(13, activity.getLieu().getLat());
            return ps;
        });
    }
    
    public int post(Activity activity) throws Exception{
        int id = findUniqueId() + 1;
        activity.setId(id);
        insert(activity);
        return id;
    }
    
    private static final String FIND_UNIQUE_ID_STMT
            = " SELECT"
            + "     MAX(id) AS max_id"
            + " FROM"
            + "     activities";
    
    public Integer findUniqueId(){
        return jdbcTemplate.queryForObject(FIND_UNIQUE_ID_STMT, new IntRowMapper());
    }
 
    
    private static final String INSERT_STMT
            = " INSERT INTO activities (id, name, description, district, venue_name, coordinates)"
            + " VALUES (?, ?, ?, ?, ?, ST_GeographyFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')'))"
            + " on conflict do nothing";

    private static final String INSERT_STMT_DATE
            = " INSERT INTO activities_date (event_date, event_id)"
            + " VALUES (?, ?)"
            + " on conflict (event_date, event_id) do nothing";

    /**
     * Effectue l'insertion de l'activity et des dates de l'activité
     *
     * @param activity
     * @throws Exception
     */
    public void insert(Activity activity) throws Exception {
        int numRowsAct = 0;
        int numRowsDate = 0;
 
       // Message: Insertion table activities avec succes
        numRowsAct = insertActivity(activity);
        System.out.println("TABLE ACTIVITIES: " + 1 + " ROW(S) AFFECTED.");

        for (int i = 0; i < activity.getDates().size(); i++) {
            numRowsDate += insertDate(activity, i);
            numRowsDate = i+1;
        }
        // Message: Insertion table activities_date avec succes
        System.out.println("TABLE ACTIVITIES_DATE: " + numRowsDate + " ROW(S) AFFECTED.");

    }

    /**
     * Insere l'activite dans la BD
     *
     * @param activity
     * @return PreparedStatement
     */
    public int insertActivity(Activity activity) {
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_STMT);
            ps.setInt(1, activity.getId());
            ps.setString(2, activity.getNom());
            ps.setString(3, activity.getDescription());
            ps.setString(4, activity.getArrondissement());
            ps.setString(5, activity.getLieu().getNom());
            ps.setDouble(6, activity.getLieu().getLng());
            ps.setDouble(7, activity.getLieu().getLat());
            return ps;
        });
    }

    /**
     * Insere une date pour l'activite demande
     *
     * @param activity
     * @param index
     * @return PreparedStatement
     * @throws Exception
     */
    public int insertDate(Activity activity, int index) throws Exception {
        Object timestamp = new java.sql.Timestamp(activity.getDates().get(index).getTime());
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_STMT_DATE);
            ps.setObject(1, timestamp);
            ps.setInt(2, activity.getId());
            return ps;
        });
    }



}



