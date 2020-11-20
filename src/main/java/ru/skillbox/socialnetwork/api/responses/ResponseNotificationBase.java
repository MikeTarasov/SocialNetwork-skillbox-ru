package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseNotificationBase {

  private int id;
  @JsonProperty("type_id")
  private int typeId;
  @JsonProperty("entity_id")
  private int entityId;
  @JsonProperty("sent_time")
  private long sentTime;
  private String info;
}
