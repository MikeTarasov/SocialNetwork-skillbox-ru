create table block_history (
    installed_rank BIGSERIAL PRIMARY KEY,
    time_block timestamp,
    person_id integer REFERENCES person (id),
    post_id integer REFERENCES post (id),
    comment_id integer REFERENCES post_comment (id),
    action integer);

