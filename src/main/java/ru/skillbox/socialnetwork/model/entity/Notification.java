package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    @Column(name = "sent_time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person personNotification;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "contact", nullable = false, columnDefinition = "varchar(255)")
    private String contact;

    @Column(name = "is_read", nullable = false)
    private int isRead;

    public long getTimeStamp() {
        return java.util.Date
                .from(time.atZone(ZoneId.systemDefault())
                        .toInstant()).getTime();
        //return time.toInstant(ZoneOffset.of(String.valueOf(ZoneId.systemDefault()))).toEpochMilli();
    }

    public Notification(NotificationType type, LocalDateTime time, Person personNotification, Long entityId, String contact, int isRead) {
        this.type = type;
        this.time = time;
        this.personNotification = personNotification;
        this.entityId = entityId;
        this.contact = contact;
        this.isRead = isRead;
    }
}