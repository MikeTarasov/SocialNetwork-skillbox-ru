create table post(
    id serial NOT NULL PRIMARY KEY,
    "time" timestamp NOT NULL,
    author_id int4 NOT NULL REFERENCES person (id),
    title text,
    post_text text,
    is_blocked int2,
    is_deleted int2);