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

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        /*Activity activity1 = new Activity(
                LocalDateTime.of(2021,01,1,10,01),
                "Biking in Bakony", ActivityType.BIKING );

        Activity activity2 = new Activity(
                LocalDateTime.of(2021,02,2,10,01),
                "Hiking at Kékestető", ActivityType.HIKING );
        Activity activity3 = new Activity(
                LocalDateTime.of(2021,03,2,10,01),
                "Running at Zebegény", ActivityType.RUNNING )*/;


        ActivityTrackerDao activityTrackerDao = new ActivityTrackerDao(dataSource);

        //activityTrackerDao.insertActivity(activity3);
        System.out.println(activityTrackerDao.findActivityById(3));
        System.out.println(activityTrackerDao.selectAllActivities());
        System.out.println(activityTrackerDao.selectActivityByType(ActivityType.BIKING));

    }
}
