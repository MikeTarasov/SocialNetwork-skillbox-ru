create table block_history (
    id serial NOT NULL PRIMARY KEY,
    "time" timestamp NOT NULL,
    person_id int4 NOT NULL REFERENCES person (id),
    post_id int4 NOT NULL REFERENCES post (id),
    comment_id int4 NOT NULL REFERENCES post_comment (id),
    action varchar(255));