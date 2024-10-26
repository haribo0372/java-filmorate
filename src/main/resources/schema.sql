DROP TABLE IF EXISTS t_film_genre;
DROP TABLE IF EXISTS t_film_user;
DROP TABLE IF EXISTS t_users_friendship;
DROP TABLE IF EXISTS t_user CASCADE;
DROP TABLE IF EXISTS t_film CASCADE;
DROP TABLE IF EXISTS t_genre CASCADE;
DROP TABLE IF EXISTS t_rating CASCADE;


CREATE TABLE IF NOT EXISTS t_genre (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS t_rating (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS t_film (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description VARCHAR(256),
    releaseDate TIMESTAMP WITH TIME ZONE NOT NULL,
    duration INTERVAL HOUR TO SECOND NOT NULL,
    rating_id BIGINT REFERENCES t_rating(id)
);

CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(128) NOT NULL,
    login VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    birthday TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS t_users_friendship (
    user_id_send BIGINT NOT NULL REFERENCES t_user(id),
    user_id_receive BIGINT NOT NULL REFERENCES t_user(id),
    confirmed BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS t_film_user (
    film_id BIGINT NOT NULL REFERENCES t_film(id),
    user_id BIGINT NOT NULL REFERENCES t_user(id)
);

CREATE TABLE IF NOT EXISTS t_film_genre (
    film_id BIGINT NOT NULL REFERENCES t_film(id),
    genre_id BIGINT NOT NULL REFERENCES t_genre(id)
);
