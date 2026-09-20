-- V2: editor role requests for api-gateway (auth_db)

CREATE TABLE editor_request (
    id           uuid                        NOT NULL,
    user_id      uuid                        NOT NULL,
    email        varchar(255)                NOT NULL,
    status       varchar(20)                 NOT NULL,
    requested_at timestamp with time zone    NOT NULL,
    reviewed_at  timestamp with time zone,
    CONSTRAINT pk_editor_request PRIMARY KEY (id),
    CONSTRAINT fk_editor_request_user FOREIGN KEY (user_id) REFERENCES auth_credential (user_id),
    CONSTRAINT uq_editor_request_user UNIQUE (user_id)
);