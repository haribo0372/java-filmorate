DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS films_users;
DROP TABLE IF EXISTS users_friendship;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;


CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description VARCHAR(256),
    releaseDate TIMESTAMP WITH TIME ZONE NOT NULL,
    duration INTERVAL HOUR TO SECOND NOT NULL,
    rating_id BIGINT REFERENCES ratings(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(128) UNIQUE NOT NULL,
    login VARCHAR(128) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    birthday TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS users_friendship (
    user_id_send BIGINT NOT NULL REFERENCES users(id),
    user_id_receive BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id_send, user_id_receive)
);

CREATE TABLE IF NOT EXISTS films_users (
    film_id BIGINT NOT NULL REFERENCES films(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS films_genres (
    film_id BIGINT NOT NULL REFERENCES films(id),
    genre_id BIGINT NOT NULL REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
);

