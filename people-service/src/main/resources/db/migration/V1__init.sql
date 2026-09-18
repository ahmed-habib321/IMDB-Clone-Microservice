-- V1: initial schema for people-service (people_db)

CREATE TABLE people (
    id            uuid            NOT NULL,
    name          varchar(255),
    slug          varchar(255),
    also_known_as jsonb,
    biography     varchar(255),
    profile_url   varchar(255),
    birth_date    date,
    death_date    date,
    birth_place   varchar(255),
    gender        varchar(255),
    height_cm     integer,
    popularity    double precision,
    imdb_id       varchar(255),
    created_at    timestamp with time zone,
    updated_at    timestamp with time zone,
    CONSTRAINT pk_people PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_people_slug ON people (slug);
CREATE INDEX idx_people_popular ON people (popularity);

CREATE TABLE "cast" (
    id             uuid        NOT NULL,
    title_id       uuid        NOT NULL,
    person_id      uuid,
    character_name varchar(255),
    billing_order  integer,
    is_voice       boolean,
    episode_count  integer,
    CONSTRAINT pk_cast PRIMARY KEY (id),
    CONSTRAINT uq_cast_title_person_character UNIQUE (title_id, person_id, character_name),
    CONSTRAINT fk_cast_person FOREIGN KEY (person_id) REFERENCES people (id)
);

CREATE TABLE crew (
    id         uuid        NOT NULL,
    title_id   uuid        NOT NULL,
    person_id  uuid,
    department varchar(255),
    job        varchar(255),
    CONSTRAINT pk_crew PRIMARY KEY (id),
    CONSTRAINT fk_crew_person FOREIGN KEY (person_id) REFERENCES people (id)
);

CREATE TABLE imdb_outbox (
    id           uuid                       NOT NULL,
    aggregate_id varchar(255)               NOT NULL,
    event_type   varchar(255)               NOT NULL,
    topic        varchar(255)               NOT NULL,
    payload      text                       NOT NULL,
    status       varchar(20)                NOT NULL,
    retry_count  integer                    NOT NULL,
    created_at   timestamp with time zone   NOT NULL,
    claimed_at   timestamp with time zone,
    published_at timestamp with time zone,
    CONSTRAINT pk_imdb_outbox PRIMARY KEY (id)
);