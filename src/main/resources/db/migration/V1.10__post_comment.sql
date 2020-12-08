create table post_comment(
    id serial NOT NULL PRIMARY KEY,
    "time" timestamp NOT NULL,
    post_id int4 NOT NULL REFERENCES post (id),
    parent_id int4 REFERENCES post_comment (id),
    author_id int4 NOT NULL REFERENCES person (id),
    comment_text text,
    is_blocked int2,
    is_deleted int2);