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

  private long id;
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
  public PersonEntityResponse(long id, String firstName, String lastName, long regDate, long birthDate,
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public long getRegDate() {
    return regDate;
  }

  public void setRegDate(long regDate) {
    this.regDate = regDate;
  }

  public long getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(long birthDate) {
    this.birthDate = birthDate;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public String getAbout() {
    return about;
  }

  public void setAbout(String about) {
    this.about = about;
  }

  public IdTitleResponse getCity() {
    return city;
  }

  public void setCity(IdTitleResponse city) {
    this.city = city;
  }

  public IdTitleResponse getCountry() {
    return country;
  }

  public void setCountry(IdTitleResponse country) {
    this.country = country;
  }

  public String getMessagesPermission() {
    return messagesPermission;
  }

  public void setMessagesPermission(String messagesPermission) {
    this.messagesPermission = messagesPermission;
  }

  public long getLastOnlineTime() {
    return lastOnlineTime;
  }

  public void setLastOnlineTime(long lastOnlineTime) {
    this.lastOnlineTime = lastOnlineTime;
  }

  public boolean isBlocked() {
    return isBlocked;
  }

  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}