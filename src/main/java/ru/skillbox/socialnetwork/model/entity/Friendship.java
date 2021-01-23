package ru.skillbox.socialnetwork.model.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Friendship {
    @Id
    private long id;
    private long srcPersonId;
    private long dstPersonId;
    private String code;
}
