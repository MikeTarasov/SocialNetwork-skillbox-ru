create table post_file(
    id serial NOT NULL PRIMARY KEY,
    post_id int4 NOT NULL REFERENCES post (id),
    name varchar(255),
    path varchar(255));