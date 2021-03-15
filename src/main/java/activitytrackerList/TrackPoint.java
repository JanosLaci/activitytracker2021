package activitytrackerList;

import java.time.LocalDate;


public class TrackPoint {
    private long trackPointId;
    LocalDate datum;
    private double latitude, longitude;


    public TrackPoint(long trackPointId, LocalDate datum, double latitude, double longitude) {
        this.trackPointId = trackPointId;
        this.datum = datum;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TrackPoint(LocalDate datum, double latitude, double longitude) {
        this.datum = datum;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getTrackPointId() {
        return trackPointId;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "trackPointId=" + trackPointId +
                ", datum=" + datum +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
