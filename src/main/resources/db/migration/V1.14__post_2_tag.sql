create table post2tag(
    id serial NOT NULL PRIMARY KEY,
    post_id int4 NOT NULL REFERENCES post (id),
    tag_id int4 NOT NULL REFERENCES tag (id));