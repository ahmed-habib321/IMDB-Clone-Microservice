-- V1: initial schema for awards-service (awards_db)

CREATE TABLE awards (
    id           uuid        NOT NULL,
    name         varchar(255),
    abbreviation varchar(255),
    country      varchar(255),
    website_url  varchar(255),
    CONSTRAINT pk_awards PRIMARY KEY (id)
);

CREATE TABLE award_nominations (
    id          uuid        NOT NULL,
    award_id    uuid        NOT NULL,
    title_id    uuid,
    person_id   uuid,
    title_name  varchar(255),
    person_name varchar(255),
    category    varchar(255),
    year        smallint,
    outcome     varchar(255),
    notes       text,
    CONSTRAINT pk_award_nominations PRIMARY KEY (id),
    CONSTRAINT fk_award_nominations_award FOREIGN KEY (award_id) REFERENCES awards (id)
);
CREATE INDEX idx_nominations_title ON award_nominations (title_id);
CREATE INDEX idx_nominations_person ON award_nominations (person_id);
CREATE INDEX idx_nominations_year ON award_nominations (year);