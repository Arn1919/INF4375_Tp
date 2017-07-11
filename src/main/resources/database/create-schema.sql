\connect mtl375;
DROP TABLE IF EXISTS activities_date;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS bixies;
DROP EXTENSION IF EXISTS Postgis;

CREATE EXTENSION Postgis;

CREATE TABLE activities (
    id int primary key,
    name text,
    description text,
    district text,
    venueName text,
    venue point
);

CREATE TABLE activities_date (
    id serial primary key,
    event_date timestamp with time zone,
    event_id int references activities(id)
);

CREATE TABLE bixies (
    id int primary key,
    station_name text,
    station_id int,
    station_state int,
    station_is_blocked boolean,
    station_under_maintenance boolean,
    station_out_of_order boolean,
    millis_last_update integer,
    millis_last_server_communication integer,
    lat decimal,
    lng decimal,
    available_terminals int,
    unavailable_terminals int,
    available_bikes int,
    unavailable_bikes int
);
