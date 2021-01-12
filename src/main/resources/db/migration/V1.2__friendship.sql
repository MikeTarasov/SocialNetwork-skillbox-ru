create table friendship(
                           id            bigserial PRIMARY KEY NOT NULL,
                           src_person_id bigint                NOT NULL REFERENCES person (id),
                           dst_person_id bigint                NOT NULL REFERENCES person (id),
                           code          varchar(255)
);