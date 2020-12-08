create table friendship (
    id serial PRIMARY KEY NOT NULL,
    src_person_id int4 NOT NULL REFERENCES person (id),
    dst_person_id int4 NOT NULL REFERENCES person (id),
    code varchar(255));