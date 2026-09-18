-- V1: initial schema for ratings-reviews-service (ratings_db)

CREATE TABLE ratings (
    id         uuid                       NOT NULL,
    user_id    uuid                       NOT NULL,
    title_id   uuid                       NOT NULL,
    score      smallint,
    rated_at   timestamp with time zone,
    updated_at timestamp with time zone,
    CONSTRAINT pk_ratings PRIMARY KEY (id),
    CONSTRAINT uq_ratings_user_title UNIQUE (user_id, title_id)
);
CREATE INDEX idx_ratings_title ON ratings (title_id);
CREATE INDEX idx_ratings_user ON ratings (user_id);

CREATE TABLE reviews (
    id               uuid                     NOT NULL,
    user_id          uuid                     NOT NULL,
    title_id         uuid                     NOT NULL,
    review_title     varchar(255),
    body             varchar(255),
    contains_spoiler boolean,
    is_approved      boolean,
    helpful_yes      integer,
    helpful_no       integer,
    created_at       timestamp with time zone,
    updated_at       timestamp with time zone,
    CONSTRAINT pk_reviews PRIMARY KEY (id)
);
CREATE INDEX idx_reviews_title ON reviews (title_id);
CREATE INDEX idx_reviews_user ON reviews (user_id);

CREATE TABLE review_helpfulness (
    user_id    uuid    NOT NULL,
    review_id  uuid    NOT NULL,
    is_helpful boolean,
    CONSTRAINT pk_review_helpfulness PRIMARY KEY (user_id, review_id),
    CONSTRAINT fk_review_helpfulness_review FOREIGN KEY (review_id) REFERENCES reviews (id)
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