create table post_like(
    id serial NOT NULL PRIMARY KEY,
    "time" timestamp,
    person_id int4 NOT NULL REFERENCES person (id),
    post_id int4 NOT NULL REFERENCES post (id));