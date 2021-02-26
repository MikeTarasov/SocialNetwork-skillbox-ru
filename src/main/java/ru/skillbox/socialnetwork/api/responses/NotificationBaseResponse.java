package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationBaseResponse {

  @JsonProperty("id")
  private long id;
  @JsonProperty("type_id")
  private long typeId;
  @JsonProperty("event_type")
  private String eventType;
  @JsonProperty("sent_time")
  private long sentTime;
  @JsonProperty("entity_id")
  private long entityId;
  @JsonProperty("info")
  private String info;
  @JsonProperty("entity_author")
  private PersonEntityResponse author;
}