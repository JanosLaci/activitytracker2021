CREATE TABLE activities (
			id BIGINT AUTO_INCREMENT,
			start_time TIMESTAMP,
			activity_desc VARCHAR(255),
			activity_type VARCHAR(255),
			PRIMARY KEY (id));

CREATE TABLE track_points (
            id BIGINT AUTO_INCREMENT,
            act_time DATE,
            latitude DOUBLE,
            longitude DOUBLE,
            activity_id BIGINT,
            PRIMARY KEY (id),
            FOREIGN KEY (activity_id) REFERENCES activities(id)
)

/*INSERT INTO activities (start_time, activity_desc, activity_type) VALUES
("2021-01-01 10:01:00", "Biking in Bakony", "BIKING"),
("2021-02-01 10:01:00", "Hiking at Kékestető", "HIKING"),
("2021-03-01 10:01:00", "Running at Zebegény", "RUNNING");*/


