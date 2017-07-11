package repositories;

import java.util.*;
import java.util.stream.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import resources.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;

@Component
public class ActivityRepository {

  @Autowired private JdbcTemplate jdbcTemplate;

  private static final String FIND_ALL_STMT =
      " select"
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
    ;

  public List<Activity> findAll() {
    return jdbcTemplate.query(FIND_ALL_STMT, new ActivityRowMapper());
  }

  private static final String FIND_BY_ID_STMT =
      " select"
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
    + "   id = ?"
    ;

  public Activity findById(int id) {
    return jdbcTemplate.queryForObject(FIND_BY_ID_STMT, new Object[]{id}, new ActivityRowMapper());
  }

  private static final String FIND_BY_NAME_STMT =
      " select"
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
    + "   ts_rank_cd(to_tsvector(name), q) desc"
    ;

  public List<Activity> findByName(String... tsterms) {
    String tsquery = Arrays.stream(tsterms).collect(Collectors.joining(" & "));
    return jdbcTemplate.query(FIND_BY_NAME_STMT, new Object[]{tsquery}, new ActivityRowMapper());
  }

  private static final String INSERT_STMT =
      " INSERT INTO activities (id, name, description, district, venueName, venue)"
    + " VALUES (?, ?, ?, ?, ?, POINT(?, ?))"
          + " on conflict do nothing" // DO SOMETHING HERE INSTEAD
  ;

  private static final String INSERT_STMT_DATE =
      " INSERT INTO activities_date (event_date, event_id)"
    + " VALUES (?, ?)"
           + " on conflict do nothing" // DO SOMETHING HERE INSTEAD
  ;

  public int insert(Activity activity) throws Exception {
    // Message: Insertion table activities avec succes
    int numRowsAct = insertActivity(activity);
    System.out.println("TABLE ACTIVITIES: " + numRowsAct + " ROW(S) AFFECTED.");
    // Message: Insertion table activities_date avec succes
    int numRowsDate = 0;
    for(int i = 0; i < activity.getDates().size(); i++){
      numRowsDate += insertDate(activity, i);
    }
    System.out.println("TABLE ACTIVITIES_DATE: " + numRowsDate + " ROW(S) AFFECTED.");
    // Return the number of rows affected altogether
    return numRowsAct + numRowsDate;
  }

  public int insertActivity(Activity activity){
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
}

class ActivityRowMapper implements RowMapper<Activity> {
  public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Activity(
        rs.getInt("id")
      , rs.getString("nom")
      , rs.getString("description")
      , rs.getString("arrondissement")
      , (ArrayList<String>)rs.getArray("dates")
      , (Lieu)rs.getObject("lieu")
    );
  }
}
