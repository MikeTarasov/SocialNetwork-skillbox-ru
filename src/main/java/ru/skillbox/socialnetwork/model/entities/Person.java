package ru.skillbox.socialnetwork.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @CreatedDate
    @Column(name = "reg_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "birth_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime birthDate;

    @Column(name = "e_mail", nullable = false)
    private String email;

    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "photo")
    private String photo;

    @Column(name = "about")
    private String about;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_approved")
    private int isApproved;

    @Column(name = "messages_permission")
    private String messagePermission;

    @Column(name = "last_online_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastOnlineTime;

    @Column(name = "is_online")
    private int isOnline;

    @Column(name = "is_blocked")
    private int isBlocked;

    @Column(name = "is_deleted")
    private int isDeleted;

    @OneToMany(mappedBy = "personNS", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationSettings> notificationSettings = new ArrayList<>();

    @OneToMany(mappedBy = "personNotification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationPersons = new ArrayList<>();

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    private List<Message> messages;

    @Transient
    @OneToMany(mappedBy = "srcPerson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendshipsSrc = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "dstPerson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendshipsDst = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "personLike", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> personLike = new ArrayList<>();

    public Person(String email, String password, String firstName, String lastName, LocalDateTime regDate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.regDate = regDate;
        this.lastOnlineTime = regDate;
        isApproved = 1;
        isBlocked = 0;
        isDeleted = 0;
    }

    public Person(long id, String firstName, String lastName, LocalDateTime regDate, LocalDateTime birthDate,
                  String email, String phone, String password, String photo, String about, String city,
                  String country, String confirmationCode, int isApproved, String messagePermission,
                  LocalDateTime lastOnlineTime, int isOnline, int isBlocked, int isDeleted) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.regDate = regDate;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.photo = photo;
        this.about = about;
        this.city = city;
        this.country = country;
        this.confirmationCode = confirmationCode;
        this.isApproved = isApproved;
        this.messagePermission = messagePermission;
        this.lastOnlineTime = lastOnlineTime;
        this.isOnline = isOnline;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
    }

    public boolean isBlocked() {
        return isBlocked == 1;
    }

    public boolean isDeleted() {
        return isDeleted == 1;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", regDate=" + regDate +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", about='" + about + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", confirmationCode='" + confirmationCode + '\'' +
                ", isApproved=" + isApproved +
                ", messagePermission='" + messagePermission + '\'' +
                ", lastOnlineTime=" + lastOnlineTime +
                ", isOnline=" + isOnline +
                ", isBlocked=" + isBlocked +
                ", isDeleted=" + isDeleted +
                ", notificationSettings=" + notificationSettings +
                ", notificationPersons=" + notificationPersons +
                ", messages=" + messages +
                ", friendshipsSrc=" + friendshipsSrc +
                ", friendshipsDst=" + friendshipsDst +
                ", personLike=" + personLike +
                '}';
    }
}