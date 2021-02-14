delete from friendship;
insert into friendship (id, src_person_id, dst_person_id, code) values (1, 7, 9, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (2, 9, 7, 'FRIEND');
insert into friendship (id, src_person_id, dst_person_id, code) values (3, 8, 9, 'REQUEST');
insert into friendship (id, src_person_id, dst_person_id, code) values (4, 6, 9, 'REQUEST');
insert into friendship (id, src_person_id, dst_person_id, code) values (6, 6, 7, 'REQUEST');
alter sequence friendship_id_seq restart with 30;