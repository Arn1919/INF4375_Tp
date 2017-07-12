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
    venue GEOGRAPHY(POINT, 4326)
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
    millis_last_update bigint,
    millis_last_server_communication bigint,
    bk boolean,
    bl boolean,
    lat decimal,
    lng decimal,
    available_terminals int,
    unavailable_terminals int,
    available_bikes int,
    unavailable_bikes int
);


INSERT INTO bixies (id, 
                    station_name, 
                    station_id, 
                    station_state,
                    station_is_blocked, 
                    station_under_maintenance, 
                    station_out_of_order,
                    millis_last_update, 
                    millis_last_server_communication, 
                    bk, 
                    bl,
                    lat, 
                    lng, 
                    available_terminals, 
                    unavailable_terminals,
                    available_bikes, 
                    unavailable_bikes)
VALUES (1, 'test', 2, 0, false, false, false, 123334634, 
12521513, false, false, 12.342525, -17.141531, 15, 0, 0, 0 );