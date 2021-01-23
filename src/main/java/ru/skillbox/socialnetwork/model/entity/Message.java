package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class Message {
    @Id
    private long id;
    private LocalDateTime time;
    private long authorId;
    private long recipientId;
    private String messageText;
    private String readStatus;
    private long dialogId;
    private int isDeleted;
}
