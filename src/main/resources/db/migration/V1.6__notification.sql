create table notification(
                             id        bigserial PRIMARY KEY NOT NULL,
                             type_id   bigint                NOT NULL REFERENCES notification_type (id),
                             sent_time timestamp             NOT NULL,
                             person_id bigint                NOT NULL REFERENCES person (id),
                             entity_id bigint,
                             contact   varchar(255),
                             is_read int2
);