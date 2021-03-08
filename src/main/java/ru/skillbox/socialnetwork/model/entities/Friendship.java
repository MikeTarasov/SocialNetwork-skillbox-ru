package ru.skillbox.socialnetwork.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;

    private String code;

    @Override
    public String toString() {
        return "Friendship{" +
                "id=" + id +
                ", srcPerson=" + srcPerson +
                ", dstPerson=" + dstPerson +
                ", code='" + code + '\'' +
                '}';
    }
}
