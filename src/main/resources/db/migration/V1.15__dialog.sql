create table dialog(
                       id           bigserial NOT NULL PRIMARY KEY,
                       owner_id     bigint    NOT NULL REFERENCES person (id),
                       unread_count int4,
                       is_deleted   int4,
                       invite_code  varchar(255)
);