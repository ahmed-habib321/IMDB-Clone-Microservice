-- V1: initial schema for news-service (news_db)

CREATE TABLE news_articles (
    id              uuid            NOT NULL,
    author_username varchar(255),
    title           varchar(255),
    slug            varchar(255),
    excerpt         varchar(255),
    body            varchar(255),
    cover_url       varchar(255),
    view_count      integer,
    is_published    boolean,
    created_at      timestamp with time zone,
    published_at    timestamp with time zone,
    updated_at      timestamp with time zone,
    CONSTRAINT pk_news_articles PRIMARY KEY (id)
);
CREATE UNIQUE INDEX idx_news_slug ON news_articles (slug);
CREATE INDEX idx_news_published ON news_articles (published_at);

CREATE TABLE news_title_tags (
    article_id uuid NOT NULL,
    title_id   uuid NOT NULL,
    CONSTRAINT pk_news_title_tags PRIMARY KEY (article_id, title_id),
    CONSTRAINT fk_news_title_tags_article FOREIGN KEY (article_id) REFERENCES news_articles (id)
);

CREATE TABLE news_people_tags (
    article_id uuid NOT NULL,
    person_id  uuid NOT NULL,
    CONSTRAINT pk_news_people_tags PRIMARY KEY (article_id, person_id),
    CONSTRAINT fk_news_people_tags_article FOREIGN KEY (article_id) REFERENCES news_articles (id)
);