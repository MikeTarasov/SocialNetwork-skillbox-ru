create table tag(
    id BIGSERIAL PRIMARY KEY,
    tag varchar(255));

create table post2tag(
    id BIGSERIAL PRIMARY KEY,
    post_id integer REFERENCES post (id),
    tag_id integer REFERENCES tag (id));