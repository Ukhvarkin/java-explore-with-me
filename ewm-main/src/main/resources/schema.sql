DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 name VARCHAR(250) NOT NULL,
 email VARCHAR(254) NOT NULL,
 CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 name VARCHAR NOT NULL,
 CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 annotation VARCHAR(2000) NOT NULL,
 category_id BIGINT REFERENCES categories (id),
 confirmed_requests BIGINT,
 created TIMESTAMP WITHOUT TIME ZONE,
 description VARCHAR(7000) NOT NULL,
 event_date TIMESTAMP WITHOUT TIME ZONE,
 initiator_id BIGINT REFERENCES users (id),
 lat FLOAT NOT NULL,
 lon FLOAT NOT NULL,
 paid BOOLEAN NOT NULL,
 participant_limit BIGINT,
 published TIMESTAMP WITHOUT TIME ZONE,
 request_moderation BOOLEAN NOT NULL,
 state VARCHAR(40) NOT NULL,
 title VARCHAR(120) NOT NULL,
 views BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS compilations(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 pinned BOOLEAN NOT NULL,
 title VARCHAR NOT NULL,
 CONSTRAINT UQ_COMPILATION_TITLE UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS requests(
 id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
 event_id BIGINT NOT NULL REFERENCES events (id),
 requester_id BIGINT NOT NULL REFERENCES users (id),
 status VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_compilations(
 event_id BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
 compilation_id BIGINT NOT NULL REFERENCES compilations (id) ON DELETE CASCADE,
 PRIMARY KEY (event_id, compilation_id)
);