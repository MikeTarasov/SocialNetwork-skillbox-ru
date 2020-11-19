create table notification_type(
    id BIGSERIAL PRIMARY KEY,
    code integer,
    name varchar(255));


create table notification(
    id BIGSERIAL PRIMARY KEY,
    type_id integer REFERENCES notification_type (id),
    sent_time timestamp,
    person_id integer REFERENCES person (id),
    entity_id integer,
    contact varchar(255),
    is_readed integer);

create table notification_settings(
    id BIGSERIAL PRIMARY KEY,
    person_id integer REFERENCES person (id),
    notification_type_id integer REFERENCES notification_type (id),
    enable integer);
