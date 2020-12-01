package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PersonEntityResponse {

  private int id;
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @JsonProperty("reg_date")
  private long regDate;
  @JsonProperty("birth_date")
  private long birthDate;
  private String email;
  private String phone;
  private String photo;
  private String about;
  private IdTitleResponse city;
  private IdTitleResponse country;
  @JsonProperty("messages_permission")
  private String messagesPermission;
  @JsonProperty("last_online_time")
  private long lastOnlineTime;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  private String token;


  // Friends ->  token = null !!!
  public PersonEntityResponse(int id, String firstName, String lastName, long regDate,
      long birthDate,
      String email, String phone, String photo, String about,
      IdTitleResponse city, IdTitleResponse country, String messagesPermission,
      long lastOnlineTime, boolean isBlocked) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.regDate = regDate;
    this.birthDate = birthDate;
    this.email = email;
    this.phone = phone;
    this.photo = photo;
    this.about = about;
    this.city = city;
    this.country = country;
    this.messagesPermission = messagesPermission;
    this.lastOnlineTime = lastOnlineTime;
    this.isBlocked = isBlocked;
  }
}