CREATE TYPE status_enum AS ENUM ('PENDING', 'PUBLISHED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    email   VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  lat FLOAT,
  lon FLOAT
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    location_id BIGINT NOT NULL REFERENCES locations(id),
    paid BOOL NOT NULL,
    participant_limit BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOL,
    state VARCHAR(32) NOT NULL,
    title VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  pinned BOOL NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events (
  compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
  event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  CONSTRAINT PK_COMPILATION_EVENTS PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
  requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events_requests (
  event_id BIGINT REFERENCES events(id) NOT NULL,
  request_id BIGINT REFERENCES requests(id) NOT NULL,
  CONSTRAINT PK_EVENTS_REQUESTS PRIMARY KEY (event_id, request_id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    text VARCHAR(1000) NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    state status_enum NOT NULL
);

CREATE INDEX author_index ON comments (author_id);
CREATE INDEX state_index ON comments (state);


