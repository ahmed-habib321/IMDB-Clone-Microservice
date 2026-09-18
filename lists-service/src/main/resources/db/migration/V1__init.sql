-- V1: initial schema for lists-service (lists_db)

CREATE TABLE watchlists (
    id         uuid                     NOT NULL,
    user_id    uuid                     NOT NULL,
    item_count integer,
    updated_at timestamp with time zone,
    CONSTRAINT pk_watchlists PRIMARY KEY (id),
    CONSTRAINT uq_watchlists_user UNIQUE (user_id)
);

CREATE TABLE watchlist_items (
    id           uuid                     NOT NULL,
    watchlist_id uuid                     NOT NULL,
    title_id     uuid                     NOT NULL,
    added_at     timestamp with time zone,
    watched      boolean,
    watched_at   timestamp with time zone,
    notes        varchar(255),
    CONSTRAINT pk_watchlist_items PRIMARY KEY (id),
    CONSTRAINT uq_watchlist_items_list_title UNIQUE (watchlist_id, title_id),
    CONSTRAINT fk_watchlist_items_watchlist FOREIGN KEY (watchlist_id) REFERENCES watchlists (id)
);

CREATE TABLE user_lists (
    id          uuid                     NOT NULL,
    user_id     uuid                     NOT NULL,
    name        varchar(255),
    description varchar(255),
    is_public   boolean,
    item_count  integer,
    created_at  timestamp with time zone,
    updated_at  timestamp with time zone,
    CONSTRAINT pk_user_lists PRIMARY KEY (id)
);
CREATE INDEX idx_user_lists_user ON user_lists (user_id);

CREATE TABLE user_list_items (
    id       uuid                     NOT NULL,
    list_id  uuid                     NOT NULL,
    title_id uuid                     NOT NULL,
    added_at timestamp with time zone,
    notes    varchar(255),
    CONSTRAINT pk_user_list_items PRIMARY KEY (id),
    CONSTRAINT uq_user_list_items_list_title UNIQUE (list_id, title_id),
    CONSTRAINT fk_user_list_items_list FOREIGN KEY (list_id) REFERENCES user_lists (id)
);