create table notification_settings(
                                      id                   bigserial PRIMARY KEY NOT NULL,
                                      person_id            bigint                NOT NULL REFERENCES person (id),
                                      notification_type_id bigint                NOT NULL REFERENCES notification_type (id),
                                      enable               int4
);