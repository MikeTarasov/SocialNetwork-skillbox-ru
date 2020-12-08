create table comment_like(
    id serial NOT NULL PRIMARY KEY,
    "time" timestamp NOT NULL,
    person_id int4 NOT NULL REFERENCES person (id),
    comment_id int4 NOT NULL REFERENCES post_comment (id));