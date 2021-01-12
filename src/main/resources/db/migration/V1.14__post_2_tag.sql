create table post2tag
(
    id      bigserial NOT NULL PRIMARY KEY,
    post_id bigint    NOT NULL REFERENCES post (id),
    tag_id  bigint    NOT NULL REFERENCES tag (id)
);