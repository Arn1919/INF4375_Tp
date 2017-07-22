
-- Connect to DB mtl375
\connect mtl375

-- Drop existing tables and extension
DROP TABLE IF EXISTS activities_date;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS bixies;
DROP TABLE IF EXISTS pistes;
DROP EXTENSION IF EXISTS Postgis;

-- Create extension Postgis
CREATE EXTENSION Postgis;

-- Create the tables
CREATE TABLE activities (
    id int primary key,
    name text,
    description text,
    district text,
    venue_name text,
    coordinates GEOGRAPHY(POINT, 4326)
);

CREATE TABLE activities_date (
    id serial primary key,
    event_date timestamp with time zone,
    event_id int references activities(id),
    constraint uq_id_date unique (event_id, event_date)
);

CREATE TABLE bixies (
    id int primary key,
    station_name text,
    station_id text,
    station_state int,
    station_is_blocked boolean,
    station_under_maintenance boolean,
    station_out_of_order boolean,
    millis_last_update bigint,
    millis_last_server_communication bigint,
    bk boolean,
    bl boolean,
    coordinates GEOGRAPHY(POINT, 4326),
    available_terminals int,
    unavailable_terminals int,
    available_bikes int,
    unavailable_bikes int
);

CREATE TABLE pistes (
    id int primary key,
    type_voie1 int,
    type_voie2 int,
    longueur int,
    nbr_voie int,
    nom_arr_ville text,
    piste GEOGRAPHY(MULTILINESTRING, 4326)
);

-- Quit database
\q
