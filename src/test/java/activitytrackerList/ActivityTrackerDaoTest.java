package activitytrackerList;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityTrackerDaoTest {

    ActivityTrackerDao activityTrackerDao;

    @BeforeEach
    void setUp() {
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

        activityTrackerDao = new ActivityTrackerDao(dataSource);
    }


    @Test
    void selectAllActivities() {

        assertEquals(3,activityTrackerDao.selectAllActivities().size());
    }

    @Test
    void insertActivity(){
        Activity activity = new Activity(
                LocalDateTime.of(2021,4,1,10,10,10),"Biking again", ActivityType.BIKING );
        activityTrackerDao.insertActivity(activity);

        assertEquals(4,activityTrackerDao.selectAllActivities().size());

    }

}