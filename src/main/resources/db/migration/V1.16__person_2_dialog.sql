create table person2dialog(
    id serial NOT NULL PRIMARY KEY,
    person_id int4 NOT NULL REFERENCES person (id),
    dialog_id int4 NOT NULL REFERENCES dialog (id));