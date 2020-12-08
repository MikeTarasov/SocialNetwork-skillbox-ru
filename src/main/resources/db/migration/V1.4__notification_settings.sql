create table notification_settings(
    id serial PRIMARY KEY NOT NULL,
    person_id int4 NOT NULL REFERENCES person (id),
    notification_type_id int4 NOT NULL REFERENCES notification_type (id),
    enable int2);