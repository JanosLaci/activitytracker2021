package activitytrackerList;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityTrackerMain {


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

        //.sql kiterjesztésű filenév V1__ -sal kezdődik
        // https://flywaydb.org/documentation/concepts/migrations#sql-based-migrations
        // The file name consists of the following parts:
        //Prefix: V for versioned (configurable), U for undo (configurable) and R for repeatable migrations (configurable)
        //Version: Version with dots or underscores separate as many parts as you like (Not for repeatable migrations)
        //Separator: __ (two underscores) (configurable)
        //Description: Underscores or spaces separate the words
        //Suffix: .sql (configurable)
        // All migrations in non-hidden directories below the specified ones are also picked up.


        Flyway flyway = Flyway.configure().locations("classpath:db/migration").dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        Activity activity1 = new Activity(
                LocalDateTime.of(2021,01,1,10,01),
                "Biking in Bakony", ActivityType.BIKING );

        Activity activity2 = new Activity(
                LocalDateTime.of(2021,02,2,10,01),
                "Hiking at Kékestető", ActivityType.HIKING );
        Activity activity3 = new Activity(
                LocalDateTime.of(2021,04,2,10,01),
                "Running at Zebegény", ActivityType.RUNNING );

        List<Activity> activityListToBeInserted = new ArrayList<>();
        activityListToBeInserted.add(activity1);
        activityListToBeInserted.add(activity2);
        activityListToBeInserted.add(activity3);


        ActivityTrackerDao activityTrackerDao = new ActivityTrackerDao(dataSource);

        //activityTrackerDao.insertActivity(activity3);

        activityTrackerDao.insertListOfActivities(activityListToBeInserted);

        System.out.println(activityTrackerDao.findActivityById(3));
        System.out.println(activityTrackerDao.selectAllActivities());
        System.out.println(activityTrackerDao.selectActivityByType(ActivityType.BIKING));
        System.out.println(activityTrackerDao.selectActivityBeforeDate(LocalDateTime.now().toLocalDate()));

    }
}
