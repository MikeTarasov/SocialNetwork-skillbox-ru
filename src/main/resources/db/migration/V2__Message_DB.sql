create table dialog(
    id BIGSERIAL PRIMARY KEY,
    owner_id integer,
    unread_count integer,
    is_deleted integer,
    invite_code integer);

create table person2dialog(
    id BIGSERIAL PRIMARY KEY,
    person_id integer REFERENCES person (id),
    dialog_id integer REFERENCES dialog (id));

create table message(
    id BIGSERIAL PRIMARY KEY,
    time_message timestamp,
    author_id integer REFERENCES person (id),
    recipient_id integer,
    message_text varchar(255),
    read_status varchar(255),
    dialog_id integer REFERENCES dialog (id),
    is_deleted integer);