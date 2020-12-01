package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;


@AllArgsConstructor
@NoArgsConstructor
public class UserIdStatusResponse {

  @JsonProperty("user_id")
  private int userId;
  private FriendStatus status;
}
