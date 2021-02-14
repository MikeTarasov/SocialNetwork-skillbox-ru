package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "person2dialog")
public class PersonToDialog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;
}