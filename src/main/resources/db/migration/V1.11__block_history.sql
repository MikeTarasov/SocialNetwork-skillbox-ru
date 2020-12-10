create table block_history(
                              id         bigserial NOT NULL PRIMARY KEY,
                              "time"     timestamp NOT NULL,
                              person_id  bigint    NOT NULL REFERENCES person (id),
                              post_id    bigint    NOT NULL REFERENCES post (id),
                              comment_id bigint    NOT NULL REFERENCES post_comment (id),
                              action     varchar(255)
);