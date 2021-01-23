package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_type_id")
    private NotificationType notificationType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private Person personNS;

    private int enable;

    public NotificationSettings(NotificationType notificationType, Person personNS, boolean isEnable) {
        this.notificationType = notificationType;
        this.personNS = personNS;
        setEnable(isEnable);
    }

    public void setEnable(boolean isEnable) {
        this.enable = isEnable ? 1 : 0;
    }
}