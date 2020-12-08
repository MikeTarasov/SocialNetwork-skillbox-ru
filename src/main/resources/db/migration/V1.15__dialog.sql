create table dialog(
    id serial NOT NULL PRIMARY KEY,
    owner_id int4 NOT NULL REFERENCES person (id),
    unread_count int4,
    is_deleted int4,
    invite_code varchar(255));