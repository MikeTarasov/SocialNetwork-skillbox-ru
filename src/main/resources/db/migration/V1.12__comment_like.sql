create table comment_like
(
    id         bigserial NOT NULL PRIMARY KEY,
    "time"     timestamp NOT NULL,
    person_id  bigint    NOT NULL REFERENCES person (id),
    comment_id bigint    NOT NULL REFERENCES post_comment (id)
);