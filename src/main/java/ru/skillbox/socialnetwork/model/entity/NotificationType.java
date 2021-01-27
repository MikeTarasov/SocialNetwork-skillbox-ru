package ru.skillbox.socialnetwork.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_type")
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "code", nullable = false)
    private long code;

    @Column(name = "name", columnDefinition = "varchar(255)")
    private String name;

    @OneToMany(mappedBy = "notificationType", orphanRemoval = true)
    @JsonBackReference
    private List<NotificationSettings> notificationSettingsList;
}