package uqam.repositories;

import uqam.resources.Lieu;
import uqam.resources.Activity;
import java.util.*;
import java.util.stream.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

@Component
public class ActivityRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_STMT
            = " select"
            + "     activities.id"
            + "   , name"
            + "   , description"
            + "   , district"
            + "   , venueName"
            + "   , venue"
            + "   , event_date"
            + " from"
            + "   activities"
            + " inner join"
            + "   activities_date"
            + " on"
            + "   activities.id = activities_date.event_id";

    public List<Activity> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new ActivityRowMapper());
    }

    private static final String FIND_BY_ID_STMT
            = " select"
            + "     activities.id"
            + "   , name"
            + "   , description"
            + "   , district"
            + "   , venueName"
            + "   , venue"
            + "   , event_date"
            + " from"
            + "   activities"
            + " inner join"
            + "   activities_date"
            + " on"
            + "   activities.id = activities_date.event_id"
            + " where"
            + "   activities.id = ?";

    public Activity findById(int id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new ActivityRowMapper());
    }

    private static final String FIND_BY_NAME_STMT
            = " select"
            + "     activities.id"
            + "   , ts_headline(name, q, 'HighlightAll = true') as name"
            + "   , description"
            + "   , district"
            + "   , venueName"
            + "   , venue"
            + "   , event_date"
            + " from"
            + "     activities"
            + "   , to_tsquery(?) as q"
            + " inner join"
            + "   activities_date"
            + " on"
            + "   activities.id = activities_date.event_id"
            + " where"
            + "   name @@ q"
            + " order by"
            + "   ts_rank_cd(to_tsvector(name), q) desc";

    public List<Activity> findByName(String... tsterms) {
        String tsquery = Arrays.stream(tsterms).collect(Collectors.joining(" & "));
        return jdbcTemplate.query(FIND_BY_NAME_STMT, new Object[]{tsquery}, new ActivityRowMapper());
    }

    private static final String INSERT_STMT
            = " INSERT INTO activities (id, name, description, district, venueName, venue)"
            + " VALUES (?, ?, ?, ?, ?, ST_GeographyFromText('SRID=4326;POINT(' || ? || ' ' || ? || ')'))"
            + " on conflict do nothing" // DO SOMETHING HERE INSTEAD
            ;

    private static final String INSERT_STMT_DATE
            = " INSERT INTO activities_date (event_date, event_id)"
            + " VALUES (?, ?)"
            + " on conflict do nothing" // DO SOMETHING HERE INSTEAD
            ;

    /**
     * Effectue l'insertion de l'activity et des dates de l'activité
     *
     * @param activity
     * @return int
     * @throws Exception
     */
    public int insert(Activity activity) throws Exception {
        int numRowsAct = 0;
        int numRowsDate = 0;
            // Message: Insertion table activities avec succes
            numRowsAct = insertActivity(activity);
            System.out.println("TABLE ACTIVITIES: " + 1 + " ROW(S) AFFECTED.");
        
      
        for (int i = 0; i < activity.getDates().size(); i++) {
          //  if (!datesExistInDatabase(activity.getId(), activity.getDates().get(i))) {
                numRowsDate += insertDate(activity, i);
            // }
        }
        // Message: Insertion table activities_date avec succes
        System.out.println("TABLE ACTIVITIES_DATE: " + numRowsDate + " ROW(S) AFFECTED.");

        return numRowsAct + numRowsDate;
    }

    /**
     * Insere l'activité dans la BD
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
            ps.setDouble(6, activity.getLieu().getLat());
            ps.setDouble(7, activity.getLieu().getLng());
            return ps;
        });
    }

    /**
     * Insère une date pour l'activité demandé
     *
     * @param activity
     * @param index
     * @return PreparedStatement
     * @throws Exception
     */
    public int insertDate(Activity activity, int index) throws Exception {
        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(activity.getDates().get(index));
        Object timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        return jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_STMT_DATE);
            ps.setObject(1, timestamp);
            ps.setInt(2, activity.getId());
            return ps;
        });
    }

//    /**
//     * Determine si l'activite existe dans la bd deja
//     *
//     * @param activity
//     * @return boolean
//     */
//    public boolean activityExistsInDatabase(Activity activity) {
//        if (findById(activity.getId()) != null) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//
//    /**
//     * Determine si la date existe dans la bd deja
//     *
//     * @param id
//     * @param date
//     * @return boolean
//     */
//    public boolean datesExistInDatabase(int id, String date) {
//        Activity databaseActivity = findById(id);
//        for(int i = 0; i < databaseActivity.getDates().size(); i++){
//            if( date.equals(databaseActivity.getDates().get(i))){
//                return true;
//            }
//        }
//        return false;
//    }

}

class ActivityRowMapper implements RowMapper<Activity> {

    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Activity(
                rs.getInt("activity.id"), 
                rs.getString("name"), 
                rs.getString("description"), 
                rs.getString("district"), 
                (ArrayList<String>) rs.getArray("event_date"), 
                new Lieu( rs.getString("venueName"),
                          rs.getDouble("venue"),
                          rs.getDouble("venue") )
        );
    }
}
