create table notification_type(
                                  id   bigserial PRIMARY KEY NOT NULL,
                                  code bigint                NOT NULL,
                                  name varchar(255)          NOT NULL
);