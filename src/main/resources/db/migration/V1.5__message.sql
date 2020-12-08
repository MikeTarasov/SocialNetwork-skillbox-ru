create table message(
    id serial PRIMARY KEY NOT NULL,
    "time" timestamp NOT NULL,
    author_id int4 NOT NULL REFERENCES person (id),
    recipient_id  int4 NOT NULL REFERENCES person (id),
    message_text text,
    read_status varchar(255),
    dialog_id int4 REFERENCES message (id),
    is_deleted int4);