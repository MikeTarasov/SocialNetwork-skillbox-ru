create table post_file(
                          id      bigserial NOT NULL PRIMARY KEY,
                          post_id bigint    NOT NULL REFERENCES post (id),
                          name    varchar(255),
                          path    varchar(255)
);