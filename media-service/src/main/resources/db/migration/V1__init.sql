-- V1: initial schema for media-service (media_db)

CREATE TABLE media_images (
    id          uuid                     NOT NULL,
    title_id    uuid,
    person_id   uuid,
    image_type  varchar(255),
    url         varchar(255),
    width       integer,
    height      integer,
    is_primary  boolean,
    uploaded_by uuid,
    uploaded_at timestamp with time zone,
    CONSTRAINT pk_media_images PRIMARY KEY (id)
);
CREATE INDEX idx_media_images_title ON media_images (title_id);
CREATE INDEX idx_media_images_person ON media_images (person_id);

CREATE TABLE trailers (
    id            uuid                     NOT NULL,
    title_id      uuid                     NOT NULL,
    name          varchar(255),
    trailer_type  varchar(255),
    youtube_key   varchar(255),
    duration_secs integer,
    language      varchar(255),
    published_at  timestamp with time zone,
    CONSTRAINT pk_trailers PRIMARY KEY (id)
);
CREATE INDEX idx_trailers_title ON trailers (title_id);

CREATE TABLE box_office (
    id            uuid    NOT NULL,
    title_id      uuid    NOT NULL,
    budget        bigint,
    opening_wknd  bigint,
    domestic      bigint,
    international bigint,
    worldwide     bigint,
    currency      varchar(255),
    source        varchar(255),
    CONSTRAINT pk_box_office PRIMARY KEY (id),
    CONSTRAINT uq_box_office_title UNIQUE (title_id)
);