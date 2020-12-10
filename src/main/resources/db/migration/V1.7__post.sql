create table post(
                     id         bigserial NOT NULL PRIMARY KEY,
                     "time"     timestamp NOT NULL,
                     author_id  bigint    NOT NULL REFERENCES person (id),
                     title      text,
                     post_text  text,
                     is_blocked int2,
                     is_deleted int2
);