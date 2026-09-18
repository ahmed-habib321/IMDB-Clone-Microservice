-- V1: initial schema for title-service (title_db)

-- ────────────────────────── base title table ──────────────────────────
CREATE TABLE title (
    id               uuid            NOT NULL,
    dtype            varchar(31)     NOT NULL,
    title_type       varchar(255),
    primary_title    varchar(255),
    original_title   varchar(255),
    slug             varchar(255),
    tagline          varchar(255),
    overview         varchar(255),
    poster_url       varchar(255),
    backdrop_url     varchar(255),
    status           varchar(255),
    release_date     date,
    runtime_mins     integer,
    budget           bigint,
    revenue          bigint,
    imdb_rating      double precision,
    vote_count       integer,
    popularity       double precision,
    adult            boolean,
    created_at       timestamp with time zone,
    updated_at       timestamp with time zone,
    CONSTRAINT pk_title PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_titles_slug ON title (slug);
CREATE INDEX idx_titles_dtype ON title (dtype);
CREATE INDEX idx_titles_popular ON title (popularity);
CREATE INDEX idx_titles_release_date ON title (release_date);

-- element collections
CREATE TABLE title_genres (
    title_id uuid          NOT NULL,
    genre    varchar(255)  NOT NULL,
    CONSTRAINT fk_title_genres_title FOREIGN KEY (title_id) REFERENCES title (id)
);
CREATE TABLE title_languages (
    title_id uuid          NOT NULL,
    language varchar(255)  NOT NULL,
    CONSTRAINT fk_title_languages_title FOREIGN KEY (title_id) REFERENCES title (id)
);
CREATE TABLE title_countries (
    title_id uuid          NOT NULL,
    country  varchar(255)  NOT NULL,
    CONSTRAINT fk_title_countries_title FOREIGN KEY (title_id) REFERENCES title (id)
);

-- ────────────────────────── JOINED subtypes ──────────────────────────
CREATE TABLE movie (
    id uuid NOT NULL,
    CONSTRAINT pk_movie PRIMARY KEY (id),
    CONSTRAINT fk_movie_title FOREIGN KEY (id) REFERENCES title (id)
);

CREATE TABLE tv_show (
    id               uuid    NOT NULL,
    network          varchar(255),
    creator_id       uuid,
    total_seasons    integer,
    total_episodes   integer,
    episode_runtime  integer,
    is_on_going      boolean,
    finished_at      date,
    CONSTRAINT pk_tv_show PRIMARY KEY (id),
    CONSTRAINT fk_tv_show_title FOREIGN KEY (id) REFERENCES title (id)
);

-- ────────────────────────── seasons & episodes ──────────────────────────
CREATE TABLE season (
    id            uuid    NOT NULL,
    season_number integer,
    title         varchar(255),
    overview      varchar(255),
    poster_url    varchar(255),
    air_date      date,
    tv_show_id    uuid,
    CONSTRAINT pk_season PRIMARY KEY (id),
    CONSTRAINT fk_season_tv_show FOREIGN KEY (tv_show_id) REFERENCES tv_show (id)
);

CREATE TABLE episode (
    id             uuid            NOT NULL,
    episode_number integer,
    title          varchar(255),
    overview       varchar(255),
    still_url      varchar(255),
    air_date       date,
    runtime_mins   integer,
    imdb_rating    double precision,
    vote_count     integer,
    writer_id      uuid,
    director_id    uuid,
    season_id      uuid,
    CONSTRAINT pk_episode PRIMARY KEY (id),
    CONSTRAINT fk_episode_season FOREIGN KEY (season_id) REFERENCES season (id)
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
