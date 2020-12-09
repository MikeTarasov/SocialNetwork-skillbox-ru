insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved, is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
	values (1, 'Elon', 'Musk', '2020-07-22 19:10:25', '1971-06-21', 'erm@mail.who', '+11111111111', 'Palo Alto', 'USA', 1, 0, 0, 0, 'ALL', '2020-12-02 22:11:27', 'Founder, CEO, lead designer of SpaceX',
	'https://assets.faceit-cdn.net/avatars/b9116578-aa4f-4b37-a549-4e8d6e49dc57_1584359836820.jpg', '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved, is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
	values (2, 'Василий', 'Голубь', '2020-09-07 18:10:15', '1999-05-01', 'golubvasiliy@mail.who', '+77777777777', 'Сызрань', 'Россия', 1, 0, 0, 0, 'ALL', '2020-12-02 23:11:27', 'В активном поиске',
	'https://avavatar.ru/images/avatars/5/avatar_cGM237pY1GnDDDsu.jpg', '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved, is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
	values (3, 'Алиса', 'Кислая', '2020-10-27 18:10:15', '1995-07-22', 'klukva@mail.who', '+70000000000', 'Санкт-Петербург', 'Нарния', 1, 0, 0, 0, 'ALL', '2020-12-03 03:11:27', 'секрет',
	'https://avatarko.ru/img/avatar/23/devushka_blondinka_22337.jpg', '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (1, 'Elon', 'Musk', '2020-07-22 19:10:25', '1971-06-21', 'erm@mail.who', '+11111111111', 'Palo Alto', 'USA', 1,
        0, 0, 0, 'ALL', '2020-12-02 22:11:27', 'Founder, CEO, lead designer of SpaceX',
        'https://assets.faceit-cdn.net/avatars/b9116578-aa4f-4b37-a549-4e8d6e49dc57_1584359836820.jpg',
        '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (2, 'Василий', 'Голубь', '2020-09-07 18:10:15', '1999-05-01', 'golubvasiliy@mail.who', '+77777777777', 'Сызрань',
        'Россия', 1, 0, 0, 0, 'ALL', '2020-12-02 23:11:27', 'В активном поиске',
        'https://avavatar.ru/images/avatars/5/avatar_cGM237pY1GnDDDsu.jpg',
        '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');
insert into person (id, first_name, last_name, reg_date, birth_date, e_mail, phone, city, country, is_approved,
                    is_blocked, is_deleted, is_online, messages_permission, last_online_time, about, photo, password)
values (3, 'Алиса', 'Кислая', '2020-10-27 18:10:15', '1995-07-22', 'klukva@mail.who', '+70000000000', 'Санкт-Петербург',
        'Нарния', 1, 0, 0, 0, 'ALL', '2020-12-03 03:11:27', 'секрет',
        'https://avatarko.ru/img/avatar/23/devushka_blondinka_22337.jpg',
        '$2y$12$PLSFQIHYOeMYfhRgxt3Kh.EuzkfBDr0pS2i/yGIEWxESI2bW12l4y');

-- password = '123456' in bcrypt12


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

insert into notification_settings (id, person_id, notification_type_id, enable) values (1, 1, 1, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (2, 1, 2, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (3, 1, 3, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (4, 1, 4, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (5, 1, 5, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (6, 2, 1, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (7, 2, 2, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (8, 2, 3, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (9, 2, 4, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (10, 2, 5, 1);
insert into notification_settings (id, person_id, notification_type_id, enable) values (11, 3, 1, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (12, 3, 2, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (13, 3, 3, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (14, 3, 4, 0);
insert into notification_settings (id, person_id, notification_type_id, enable) values (15, 3, 5, 0);