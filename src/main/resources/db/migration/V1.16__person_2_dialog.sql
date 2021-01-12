create table person2dialog
(
    id        bigserial NOT NULL PRIMARY KEY,
    person_id bigint    NOT NULL REFERENCES person (id),
    dialog_id bigint    NOT NULL REFERENCES dialog (id)
);