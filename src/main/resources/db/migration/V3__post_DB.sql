create table post(
    id BIGSERIAL PRIMARY KEY,
    time timestamp,
    author_id integer REFERENCES person (id),
    title varchar(255),
    post_text varchar(2048),
    is_blocked smallint,
    is_deleted smallint);

create table post_like(
    id BIGSERIAL PRIMARY KEY,
    post_id integer REFERENCES post (id),
    person_id integer REFERENCES person (id));

create table post_file(
    id BIGSERIAL PRIMARY KEY,
    post_id integer REFERENCES post (id),
    name varchar(255),
    path varchar(255));