package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntityResponse {

  private long id;
  @JsonProperty("time")
  private long timestamp;
  @JsonProperty("author_id")
  private long authorId;
  @JsonProperty("recipient")
  private PersonEntityResponse recipient;
  @JsonProperty("message_text")
  private String messageText;
  @JsonProperty("read_status")
  private String readStatus;  //TODO readStatus = enum [SENT, READ]
}
