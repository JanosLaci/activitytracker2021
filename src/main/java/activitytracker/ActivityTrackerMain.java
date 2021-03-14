package activitytracker;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {


    void insertActivity(DataSource dataSource, Activity inputActivity){

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "INSERT INTO activities (start_time, activity_desc, activity_type) VALUES (?,?,?)")
        ) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(inputActivity.getStartTime()));
            preparedStatement.setString(2,inputActivity.getDesc());
            preparedStatement.setString(3, inputActivity.getType().toString());

            preparedStatement.executeUpdate();
        }
        catch (SQLException se) {
            throw new IllegalStateException("Cannot insert", se);
        }

    }

    /* kiszervezés előtt id alapján

    public Activity findActivityById(DataSource dataSource, long inputId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("select * from activities where id = ?");
        ){
            preparedStatement.setLong(1, inputId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Activity activityFromDatabase = new Activity(
                            resultSet.getLong("id"),
                            resultSet.getTimestamp("start_time").toLocalDateTime(),
                            resultSet.getString("activity_desc"),
                            ActivityType.valueOf(resultSet.getString("activity_type")));
                    return activityFromDatabase;
                }
                throw new IllegalArgumentException("Id not found");
            }
        }
        catch (SQLException se) { throw new IllegalStateException("Cannot query", se); }
    }*/


    private Activity selectActivityByPreparedStatement(PreparedStatement preparedStatement){

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                Activity activityFromDatabase = new Activity(
                        resultSet.getLong("id"),
                        resultSet.getTimestamp("start_time").toLocalDateTime(),
                        resultSet.getString("activity_desc"),
                        ActivityType.valueOf(resultSet.getString("activity_type")));
                return activityFromDatabase;
            }
            throw new IllegalArgumentException("Id not found");
        }
        catch (SQLException se) { throw new IllegalArgumentException("Connection failed", se); }
    }

    public Activity findActivityById(DataSource dataSource, long inputId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM activities WHERE id = ?");
        ){
            preparedStatement.setLong(1, inputId);
            return selectActivityByPreparedStatement(preparedStatement);

        }
        catch (SQLException se) { throw new IllegalStateException("Cannot query", se); }
    }


    public List<Activity> selectAllActivities(DataSource dataSource) {
        List<Activity> activityListFromDatabase = new ArrayList<>();

        //nincs paraméter, try fejlécen belül létrehozható a ResultSet is

        try (
                Connection conn = dataSource.getConnection();
                Statement connStatement = conn.createStatement();
                ResultSet resultSet = connStatement.executeQuery("SELECT * FROM activities ORDER BY id")
        ) {
            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                Activity activityFromDatabase = new Activity(
                        resultSet.getLong("id"),
                        resultSet.getTimestamp("start_time").toLocalDateTime(),
                        resultSet.getString("activity_desc"),
                        ActivityType.valueOf(resultSet.getString("activity_type")));
                activityListFromDatabase.add(activityFromDatabase);
            }
            return activityListFromDatabase;
        }
        catch (SQLException se) { throw new IllegalStateException("Cannot select employees", se); }
    }









    public static void main(String[] args) {

        MariaDbDataSource dataSource;
        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
            dataSource.setUser("activitytracker");
            dataSource.setPassword("pass");
        }
        catch (SQLException se) {
            throw new IllegalStateException("Connection failed", se);
        }

        Activity activity1 = new Activity(
                LocalDateTime.of(2021,01,1,10,01),
                "Biking in Bakony", ActivityType.BIKING );

        Activity activity2 = new Activity(
                LocalDateTime.of(2021,02,2,10,01),
                "Hiking at Kékestető", ActivityType.HIKING );
        Activity activity3 = new Activity(
                LocalDateTime.of(2021,03,2,10,01),
                "Running at Zebegény", ActivityType.RUNNING );


        ActivityTrackerMain activityTrackerMain = new ActivityTrackerMain();

        //activityTrackerMain.insertActivity(dataSource, activity3);
        System.out.println(activityTrackerMain.findActivityById(dataSource, 2));
        System.out.println(activityTrackerMain.selectAllActivities(dataSource));




    }




}
