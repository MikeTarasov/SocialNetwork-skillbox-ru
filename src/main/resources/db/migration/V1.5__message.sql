create table message
(
    id           bigserial PRIMARY KEY NOT NULL,
    "time"       timestamp             NOT NULL,
    author_id    bigint                NOT NULL REFERENCES person (id),
    recipient_id bigint                NOT NULL REFERENCES person (id),
    message_text text,
    read_status  varchar(255),
    dialog_id    bigint REFERENCES message (id),
    is_deleted   int2
);