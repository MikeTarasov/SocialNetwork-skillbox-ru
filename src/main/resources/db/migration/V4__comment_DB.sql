create table post_comment(
    id BIGSERIAL PRIMARY KEY,
    time timestamp,
    post_id bigint REFERENCES post (id),
    parent_id bigint,
    author_id integer REFERENCES person (id),
    comment_text varchar(255),
    is_blocked smallint,
    is_deleted smallint);

create table comment_like(
    id BIGSERIAL PRIMARY KEY,
    time_like timestamp,
    person_id integer REFERENCES person (id),
    comment_id bigint REFERENCES post_comment (id));