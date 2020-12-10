create table post_comment(
                             id           bigserial NOT NULL PRIMARY KEY,
                             "time"       timestamp NOT NULL,
                             post_id      bigint    NOT NULL REFERENCES post (id),
                             parent_id    bigint REFERENCES post_comment (id),
                             author_id    bigint    NOT NULL REFERENCES person (id),
                             comment_text text,
                             is_blocked   int2,
                             is_deleted   int2
);