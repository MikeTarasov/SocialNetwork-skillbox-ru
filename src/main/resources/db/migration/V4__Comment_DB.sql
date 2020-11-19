create table post_comment(
    id BIGSERIAL PRIMARY KEY,
    time_comment timestamp,
    post_id integer REFERENCES post (id),
    parent_id integer,
    author_id integer REFERENCES person (id),
    comment_text varchar(255),
    is_blocked integer,
    is_deleted integer);

create table comment_like(
    id BIGSERIAL PRIMARY KEY,
    time_like timestamp,
    person_id integer REFERENCES person (id),
    comment_id integer REFERENCES post_comment (id));