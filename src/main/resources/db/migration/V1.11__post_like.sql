create table post_like
(
    id        bigserial NOT NULL PRIMARY KEY,
    "time"    timestamp,
    person_id bigint    NOT NULL REFERENCES person (id),
    post_id   bigint    NOT NULL REFERENCES post (id)
);