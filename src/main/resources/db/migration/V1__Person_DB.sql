create table person (
    id BIGSERIAL PRIMARY KEY,
    first_name varchar(255),
    last_name varchar(255),
    reg_date timestamp,
    birth_date timestamp,
    e_mail varchar(255),
    phone varchar(255),
    password varchar(255),
    photo varchar(255),
    about varchar(255),
    city varchar(255),
    country varchar(255),
    confirmation_code varchar(255),
    is_approved integer,
    message_permission varchar(255),
    last_online_time timestamp,
    is_blocked integer,
    is_online integer,
    is_delete integer);

create table friendship (
    id BIGSERIAL PRIMARY KEY,
    src_person_id integer,
    dst_person_id integer,
    code varchar(255));

