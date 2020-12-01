package ru.skillbox.socialnetwork.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "notification_type")
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false)
    private Integer code;

    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;
}
