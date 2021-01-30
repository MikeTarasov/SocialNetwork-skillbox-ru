delete from message;
delete from person2dialog;
delete from dialog;

insert into dialog (id, owner_id, unread_count, is_deleted, invite_code)
values (1, 9, 0, 0, 'xxxxxxx');
insert into dialog (id, owner_id, unread_count, is_deleted, invite_code)
values (2, 8, 0, 0, 'xxxxxxx');
insert into dialog (id, owner_id, unread_count, is_deleted, invite_code)
values (3, 9, 0, 0, 'xxxxxxx');

insert into person2dialog (id, person_id, dialog_id)
values (1, 9, 1);
insert into person2dialog (id, person_id, dialog_id)
values (2, 8, 1);
insert into person2dialog (id, person_id, dialog_id)
values (3, 9, 2);
insert into person2dialog (id, person_id, dialog_id)
values (4, 8, 2);
insert into person2dialog (id, person_id, dialog_id)
values (5, 8, 3);
insert into person2dialog (id, person_id, dialog_id)
values (6, 9, 3);

insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (1, '2020-12-03 03:11:27', 8, 9, 'text 1', 'SENT', 1, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (2, '2020-12-03 03:11:28', 8, 9, 'text 2', 'SENT', 2, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (3, '2020-12-03 03:11:29', 8, 9, 'text 3', 'SENT', 3, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (4, '2020-12-03 03:11:30', 8, 9, 'text 4', 'SENT', 1, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (5, '2020-12-03 03:11:31', 8, 9, 'text 5', 'SENT', 2, 0);

insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (6, '2020-12-03 03:11:32', 9, 8, 'text 6', 'SENT', 1, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (7, '2020-12-03 03:11:33', 8, 9, 'text 7', 'READ', 1, 0);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (8, '2020-12-03 03:11:34', 8, 9, 'text 8', 'READ', 1, 0);