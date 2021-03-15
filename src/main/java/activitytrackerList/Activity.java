package activitytrackerList;


//SQL
//create schema if not exists activitytracker default character set utf8 collate utf8_hungarian_ci;
//
//create user 'activitytracker'@'localhost' identified by 'activitytracker';
//grant all on *.* to 'activitytracker'@'localhost';

//CREATE TABLE activities (
//			id BIGINT AUTO_INCREMENT,
//			start_time TIMESTAMP,
//			activity_desc VARCHAR(255),
//			activity_type VARCHAR(255),
//			PRIMARY KEY (id));

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Activity {

    private long id;
    private LocalDateTime startTime;
    private String desc;
    private ActivityType type;
    private List<TrackPoint> trackPoints = new ArrayList<>();


    public Activity(long id, LocalDateTime startTime, String desc, ActivityType type) {
        this.id = id;
        this.startTime = startTime;
        this.desc = desc;
        this.type = type;
    }

    public Activity(LocalDateTime startTime, String desc, ActivityType type) {
        this.startTime = startTime;
        this.desc = desc;
        this.type = type;
    }


    public void addTrackPoint(TrackPoint trackPoint){
        trackPoints.add(trackPoint);

    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDesc() {
        return desc;
    }

    public ActivityType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", desc='" + desc + '\'' +
                ", type=" + type +
                '}';
    }
}
