delete from notification;
delete from message;
delete from person2dialog;
delete from person;
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (7, 'Гном', 'Садовый', '2020-04-02 12:20:11', '1986-01-01', 'gnom@mail.who', '+7777777777', 'Мория',
        'Средиземье', 1, 0, 0, 0, 'ALL', '2020-09-02 23:10:27', 'Йо-хо-хо',
        'https://avatarko.ru/img/avatar/28/gnom_27031.jpg',
        '$2y$12$dPvFbSryXmRxerHZHjFPh.WW0uQe6sRiPDiHEaiqveRWNOh2NPaHS');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (8, 'Дед', 'Мороз', '2020-09-07 18:10:15', '1999-05-01', 'dedm@mail.who', '+888888888', 'Великие Луки',
        'Россия', 1, 0, 0, 0, 'ALL', '2020-12-02 23:11:27', 'Борода из ваты!',
        'https://avatarko.ru/img/avatar/25/spinoj_Novyj_god_Ded_Moroz_Snegurochka_24185.jpg',
        '$2y$12$dPvFbSryXmRxerHZHjFPh.WW0uQe6sRiPDiHEaiqveRWNOh2NPaHS');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (9, 'Котик', 'Чеширский', '2020-07-22 19:10:25', '1971-06-21', 'shred@mail.who', '+999999999', 'Book',
        'Wonderland', 1, 0, 0, 1, 'ALL', '2020-12-02 22:11:27', 'мяу',
        'https://avatarko.ru/img/avatar/18/kot_multfilm_17215.jpg',
        '$2y$12$dPvFbSryXmRxerHZHjFPh.WW0uQe6sRiPDiHEaiqveRWNOh2NPaHS');