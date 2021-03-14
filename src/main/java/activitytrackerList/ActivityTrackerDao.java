package activitytrackerList;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerDao {

    //már nem kell a metódusokban a dataSource paraméter, benne van az osztályban

    private DataSource dataSource;


    public ActivityTrackerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    void insertActivity(Activity inputActivity){

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



    private List<Activity> selectActivityByPreparedStatement(PreparedStatement preparedStatement){
        List<Activity> activityListFromDatabase = new ArrayList<>();

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
        catch (SQLException se) { throw new IllegalArgumentException("Connection failed", se); }
    }

    public Activity findActivityById(long inputId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM activities WHERE id = ?");
        ) {
            preparedStatement.setLong(1, inputId);
            List<Activity> resultList = selectActivityByPreparedStatement(preparedStatement);

            if (resultList.size() == 1) return selectActivityByPreparedStatement(preparedStatement).get(0);
            else throw new IllegalStateException("Wrong id");
        }
        catch (SQLException se) { throw new IllegalStateException("Cannot query", se); }
    }


    public List<Activity> selectAllActivities() {

        //nincs paraméter, try fejlécen belül létrehozható a ResultSet is

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement connStatement = conn.prepareStatement("SELECT * FROM activities ORDER BY id");
        ) {
            return selectActivityByPreparedStatement(connStatement);
        }
        catch (SQLException se) { throw new IllegalStateException("Cannot select employees", se); }
    }

    public List<Activity> selectActivityByType(ActivityType activityType) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM activities WHERE activity_type = ?")){
                 preparedStatement.setString(1, activityType.toString());
                 return selectActivityByPreparedStatement(preparedStatement);

        } catch (SQLException sqlException) {
            throw new IllegalStateException("Connection failed.", sqlException);
        }


    }



}
