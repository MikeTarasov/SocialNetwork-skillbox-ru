delete from friendship;
insert into friendship (id, src_person_id, dst_person_id, code) values (1, 7, 9, 'DECLINED');
insert into friendship (id, src_person_id, dst_person_id, code) values (2, 8, 9, 'BLOCKED');
alter sequence friendship_id_seq restart with 30;