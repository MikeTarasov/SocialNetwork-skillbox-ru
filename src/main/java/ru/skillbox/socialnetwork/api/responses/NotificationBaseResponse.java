package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class NotificationBaseResponse {

  private long id;
  @JsonProperty("type_id")
  private long typeId;
  @JsonProperty("sent_time")
  private long sentTime;
  @JsonProperty("entity_id")
  private long entityId;
  private String info;
}