package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ResponsePerson {

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
  private ResponseCityCountry city;
  private ResponseCityCountry country;
  @JsonProperty("messages_permission")
  private String messagesPermission;
  @JsonProperty("last_online_time")
  private long lastOnlineTime;
  @JsonProperty("is_blocked")
  private boolean isBlocked;
  private String token;


  // Friends  token = null !!!
  public ResponsePerson(int id, String firstName, String lastName, long regDate, long birthDate,
      String email, String phone, String photo, String about,
      ResponseCityCountry city, ResponseCityCountry country, String messagesPermission,
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