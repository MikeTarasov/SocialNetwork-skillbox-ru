delete
from post_comment;
insert into post_comment (id, time, parent_id, comment_text, is_blocked, is_deleted, author_id, post_id)
values (1, '2020-08-24 01:02:51', null, 'Very good!', 0, 0, 8, 1);
alter sequence post_comment_id_seq restart with 2;