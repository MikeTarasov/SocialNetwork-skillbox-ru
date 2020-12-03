package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    @Column(name = "sent_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "contact", nullable = false, columnDefinition = "varchar(255)")
    private String contact;

    @Column(name = "is_read", nullable = false)
    private byte isRead;

}
