package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;

    private String code;
}
