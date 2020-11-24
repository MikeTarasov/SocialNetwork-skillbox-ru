package ru.skillbox.socialnetwork.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Data
@Table(name = "person")
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @CreatedDate
  @Column(name = "reg_date", nullable = false, columnDefinition = "TIMESTAMP")
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

  @Column(name = "message_permission")
  private String messagePermission;

  @Column(name = "last_online_time", columnDefinition = "TIMESTAMP")
  private LocalDateTime lastOnlineTime;

  @Column(name = "is_online")
  private int isOnline;

  @Column(name = "is_blocked")
  private int isBlocked;

  @Column(name = "is_delete")
  private int isDelete;

}
