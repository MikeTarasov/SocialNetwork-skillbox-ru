package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.enums.MessagesPermissions;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonEditRequest {

  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @JsonProperty("birth_date")
  private long birthDate;
  private String phone;
  @JsonProperty("photo_id")
  private int photoId;
  private String about;
  @JsonProperty("town_id")
  private int townId;
  @JsonProperty("country_id")
  private int countryId;
  @JsonProperty("messages_permission")
  private MessagesPermissions messagesPermission;

//  photo_id	number
//  example: o1doj1d91j1d01d-1d1f  //TODO  o1doj1d91j1d01d-1d1f -> NUMBER??????????
//  ID на фото в хранилище

//  messages_permission	string //TODO test enum!
}