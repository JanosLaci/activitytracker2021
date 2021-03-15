package activitytrackerList;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerDao {

    //már nem kell a metódusokban a dataSource paraméter, benne van az osztályban

    private DataSource dataSource;


    public ActivityTrackerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Activity insertActivity(Activity inputActivity){

        // Statement.RETURN_GENERATED_KEYS helyett elég "1"

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "INSERT INTO activities (start_time, activity_desc, activity_type) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(inputActivity.getStartTime()));
            preparedStatement.setString(2,inputActivity.getDesc());
            preparedStatement.setString(3, inputActivity.getType().toString());

            preparedStatement.executeUpdate();

            return getIdAfterExecution(inputActivity, preparedStatement);
        }
        catch (SQLException se) {
            throw new IllegalStateException("Cannot insert", se);
        }

    }

    //Activity lista betöltése adatbázisba tranzakcióval

    public void insertListOfActivities(List<Activity> inputActivityList) {

        try (Connection dataSourceConnection = dataSource.getConnection();
        ) {
            dataSourceConnection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = dataSourceConnection.prepareStatement(
                    "INSERT INTO activities (start_time, activity_desc, activity_type) VALUES (?,?,?)")
            ) {
                for (Activity inputActivity : inputActivityList) {
                    if (inputActivity.getDesc() == null) {
                        throw new IllegalArgumentException("Description cannot be null");
                    }

                    preparedStatement.setTimestamp(1, Timestamp.valueOf(inputActivity.getStartTime()));
                    preparedStatement.setString(2, inputActivity.getDesc());
                    preparedStatement.setString(3, inputActivity.getType().toString());
                    preparedStatement.executeUpdate();
                }
                dataSourceConnection.commit();
            } catch (IllegalArgumentException illegalArgumentException) {

                dataSourceConnection.rollback();
            }
        } catch (SQLException sqlException) {

            throw new IllegalStateException("Cannot insert", sqlException);
        }


    }



    private Activity getIdAfterExecution(Activity inputActivity, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next()){
                long id = resultSet.getLong(1);
                return new Activity(id,
                        inputActivity.getStartTime(),
                        inputActivity.getDesc(),
                        inputActivity.getType()
                        );

            }
        }
        throw new IllegalStateException("Cannot get keys");
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

    public List<Activity> selectActivityBeforeDate(LocalDate localDate) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(
                        "SELECT * FROM activities WHERE start_time < ?");
        ) {
            //vagy: .atTime
            // vagy: setString és toString, de lehet, h adatbáziskezelő-függő
            // preparedStatement.setTimestamp(1, Timestamp.valueOf(localDate.toString())); esetén:
            //[Activity{id=1, startTime=2021-01-01T10:01, desc='Biking in Bakony', type=BIKING}]
            //Exception in thread "main" java.lang.IllegalArgumentException:
            // Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]

            preparedStatement.setTimestamp(1, Timestamp.valueOf(localDate.atStartOfDay()));
            return selectActivityByPreparedStatement(preparedStatement);
        }
        catch (SQLException se) { throw new IllegalStateException("Cannot select employees", se); }
    }



}
