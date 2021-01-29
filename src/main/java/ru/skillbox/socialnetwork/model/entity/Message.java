package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false, columnDefinition = "timestamp")
    private LocalDateTime time;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private Person recipient;

    @Column(name = "message_text", columnDefinition = "varchar(255)")
    private String text;

    @Column(name = "read_status", columnDefinition = "varchar(255)")
    private String readStatus;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

    @Column(name = "is_deleted")
    private int isDeleted;

}
