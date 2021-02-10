delete from friendship;
-- 9 - current user. friends: 7 and 8, declined:6
insert into friendship (id, src_person_id, dst_person_id, code) values (1, 7, 9, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (2, 9, 7, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (3, 8, 9, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (4, 9, 8, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (5, 6, 9, 'SUBSCRIBED');
insert into friendship (id, src_person_id, dst_person_id, code) values (6, 9, 6, 'DECLINED');
-- 7 has friendship with 4 and 8
insert into friendship (id, src_person_id, dst_person_id, code) values (7, 7, 4, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (8, 4, 7, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (9, 7, 8, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (10, 8, 7, 'FRIEND');
-- 7 declined 3 and 5
insert into friendship (id, src_person_id, dst_person_id, code) values (11, 7, 3, 'DECLINED');
insert into friendship (id, src_person_id, dst_person_id, code) values (12, 3, 7, 'SUBSCRIBED');
insert into friendship (id, src_person_id, dst_person_id, code) values (13, 7, 5, 'DECLINED');
insert into friendship (id, src_person_id, dst_person_id, code) values (18, 5, 7, 'SUBSCRIBED');
-- 8 has friendship with 4, 5 and 6
insert into friendship (id, src_person_id, dst_person_id, code) values (14, 8, 5, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (15, 5, 8, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (16, 8, 6, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (17, 6, 8, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (19, 8, 4, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (20, 4, 8, 'FRIEND');
-- expected rec: 4 (from 7) and 5 (from 8)
alter sequence friendship_id_seq restart with 30;