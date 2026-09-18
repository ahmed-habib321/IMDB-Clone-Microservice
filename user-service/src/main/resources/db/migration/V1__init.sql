-- V1: initial schema for user-service (user_db)

CREATE TABLE users (
    id         uuid                       NOT NULL,
    email      varchar(255)               NOT NULL,
    username   varchar(255),
    created_at timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE user_profile (
    id            uuid                     NOT NULL,
    user_id       uuid,
    display_name  varchar(255),
    avatar_url    varchar(255),
    bio           varchar(255),
    country       varchar(255),
    birth_date    date,
    gender        varchar(255),
    website_url   varchar(255),
    total_ratings integer,
    total_reviews integer,
    member_since  timestamp with time zone,
    CONSTRAINT pk_user_profile PRIMARY KEY (id)
);

CREATE TABLE user_preferences (
    id               uuid    NOT NULL,
    user_id          uuid,
    fav_genres       jsonb,
    fav_languages    jsonb,
    adult_content    boolean,
    email_notifs     boolean,
    public_watchlist boolean,
    CONSTRAINT pk_user_preferences PRIMARY KEY (id)
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