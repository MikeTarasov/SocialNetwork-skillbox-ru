create table notification_type(
    id serial PRIMARY KEY,
    code integer,
    name varchar(255));


create table notification(
    id serial PRIMARY KEY,
    type_id integer REFERENCES notification_type (id),
    sent_time timestamp,
    person_id integer REFERENCES person (id),
    entity_id integer,
    contact varchar(255),
    is_read smallint);

create table notification_settings(
    id serial PRIMARY KEY,
    person_id integer REFERENCES person (id),
    notification_type_id integer REFERENCES notification_type (id),
    enable integer);
