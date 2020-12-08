create table notification(
    id serial PRIMARY KEY NOT NULL,
    type_id int4 NOT NULL REFERENCES notification_type (id),
    sent_time timestamp NOT NULL,
    person_id int4 NOT NULL REFERENCES person (id),
    entity_id int4,
    contact varchar(255),
    is_readed int2);