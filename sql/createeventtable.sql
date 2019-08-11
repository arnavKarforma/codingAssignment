CREATE TABLE IF NOT EXISTS Events (
    eventId varchar(255) NOT NULL,
    host varchar(255),
    type varchar(255),
    eventDuration int,
	alert boolean,
    PRIMARY KEY (eventId)
);