package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;

@Data
@AllArgsConstructor
public class ResponseDataFriends {

  @JsonProperty("user_id")
  private int userId;
  private FriendStatus status;
}
