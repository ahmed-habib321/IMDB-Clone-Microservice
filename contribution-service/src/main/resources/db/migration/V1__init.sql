-- V1: initial schema for contribution-service (contribution_db)

CREATE TABLE trivia (
    id             uuid                     NOT NULL,
    title_id       uuid                     NOT NULL,
    body           text,
    is_spoiler     boolean,
    is_approved    boolean,
    helpful_count  integer,
    contributed_by uuid,
    created_at     timestamp with time zone,
    updated_at     timestamp with time zone,
    CONSTRAINT pk_trivia PRIMARY KEY (id)
);
CREATE INDEX idx_trivia_title ON trivia (title_id);

CREATE TABLE quotes (
    id             uuid                     NOT NULL,
    title_id       uuid                     NOT NULL,
    body           text,
    is_spoiler     boolean,
    is_approved    boolean,
    helpful_count  integer,
    contributed_by uuid,
    created_at     timestamp with time zone,
    updated_at     timestamp with time zone,
    spoken_by      varchar(255),
    CONSTRAINT pk_quotes PRIMARY KEY (id)
);
CREATE INDEX idx_quotes_title ON quotes (title_id);

CREATE TABLE goofs (
    id             uuid                     NOT NULL,
    title_id       uuid                     NOT NULL,
    body           text,
    is_spoiler     boolean,
    is_approved    boolean,
    helpful_count  integer,
    contributed_by uuid,
    created_at     timestamp with time zone,
    updated_at     timestamp with time zone,
    goof_type      varchar(255),
    CONSTRAINT pk_goofs PRIMARY KEY (id)
);
CREATE INDEX idx_goofs_title ON goofs (title_id);

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