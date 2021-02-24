--Добавляем типы оповещений
insert into notification_type (id, code, name)
values (1, 1, 'POST');
insert into notification_type (id, code, name)
values (2, 2, 'POST_COMMENT');
insert into notification_type (id, code, name)
values (3, 3, 'COMMENT_COMMENT');
insert into notification_type (id, code, name)
values (4, 4, 'FRIEND_REQUEST');
insert into notification_type (id, code, name)
values (5, 5, 'MESSAGE');
insert into notification_type (id, code, name)
values (6, 6, 'FRIEND_BIRTHDAY');

--добавляем 2-х людей
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (8, 'Дед', 'Мороз', '2020-09-07 18:10:15', '1999-05-01', 'dedm@mail.who', '+888888888', 'Великие Луки',
        'Россия', 1, 0, 0, 0, 'ALL', '2020-12-02 23:11:27', 'Борода из ваты!',
        'https://avatarko.ru/img/avatar/25/spinoj_Novyj_god_Ded_Moroz_Snegurochka_24185.jpg',
        '$2y$12$dPvFbSryXmRxerHZHjFPh.WW0uQe6sRiPDiHEaiqveRWNOh2NPaHS');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (9, 'Котик', 'Чеширский', '2020-07-22 19:10:25', '1971-06-21', 'shred@mail.who', '+999999999', 'Book',
        'Wonderland', 1, 0, 0, 0, 'ALL', '2020-12-02 22:11:27', 'мяу',
        'https://avatarko.ru/img/avatar/18/kot_multfilm_17215.jpg',
        '$2y$12$dPvFbSryXmRxerHZHjFPh.WW0uQe6sRiPDiHEaiqveRWNOh2NPaHS');

--добавляем настройки оповещений этим людям
insert into notification_settings (id, person_id, notification_type_id, enable)
values (1, 9, 1, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (2, 9, 2, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (3, 9, 3, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (4, 9, 4, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (5, 9, 5, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (6, 9, 6, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (7, 8, 1, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (8, 8, 2, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (9, 8, 3, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (10, 8, 4, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (11, 8, 5, 1);
insert into notification_settings (id, person_id, notification_type_id, enable)
values (12, 8, 6, 1);

--добавляем пост, написанный 9 чуваком
insert into post (id, time, author_id, is_blocked, is_deleted, title, post_text) values (1, '2020-07-23 00:05:52', 9, 0, 0, 'Hello', 'Brand is just a perception, and perception will match reality over time. Sometimes it will be ahead, other times it will be behind. But brand is simply a collective impression some have about a product.');

--добавляем комментарий к посту от 8 чувака и оповещение об этом для 9-го чувака
insert into post_comment (id, time, parent_id, comment_text, is_blocked, is_deleted, author_id, post_id)
values (1, '2020-08-24 01:02:51', null, 'Very good!', 0, 0, 8, 1);

insert into notification (id, type_id, sent_time, person_id, entity_id, contact, is_read)
values (1, 2, '2020-08-24 01:02:51', 9, 1, 'shred@mail.who', 0);

--добавляем ответ 9-го чувака на комментарий 8-го чувака и оповещение об этом для 8-го чувака
insert into post_comment (id, time, parent_id, comment_text, is_blocked, is_deleted, author_id, post_id)
values (2, '2020-08-24 01:07:51', 1, 'Thanks!', 0, 0, 9, 1);

insert into notification (id, type_id, sent_time, person_id, entity_id, contact, is_read)
values (2, 3, '2020-08-24 01:07:51', 8, 2, 'dedm@mail.who', 0);

--добавляем 8-го чувака в друзья и оповещение об этом для 8-го чувака
insert into friendship (id, src_person_id, dst_person_id, code) values (1, 9, 8, 'FRIEND');
insert into notification (id, type_id, sent_time, person_id, entity_id, contact, is_read)
values (3, 4, '2020-08-24 01:08:51', 8, 1, 'dedm@mail.who', 0);

--пишем 8-му чуваку сообщение и оповещение об этом для 8-го чувака
insert into dialog (id, owner_id, unread_count, is_deleted, invite_code)
values (1, 9, 0, 0, 'xxxxxxx');
insert into person2dialog (id, person_id, dialog_id)
values (1, 9, 1);
insert into message(id, time, author_id, recipient_id, message_text, read_status, dialog_id, is_deleted)
values (1, '2020-08-24 01:09:51', 9, 8, 'HI!', 'SENT', 1, 0);
insert into notification (id, type_id, sent_time, person_id, entity_id, contact, is_read)
values (4, 5, '2020-08-24 01:09:51', 8, 1, 'dedm@mail.who', 0);

insert into notification (id, type_id, sent_time, person_id, entity_id, contact, is_read)
values (5, 6, '2020-08-24 01:10:51', 9, 8, 'shred@mail.who', 0);

