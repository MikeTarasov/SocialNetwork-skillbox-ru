package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class MessageEntityResponse {

  private int id;
  private long timestamp;
  @JsonProperty("author_id")
  private int authorId;
  @JsonProperty("recipient_id")
  private int recipientId;
  @JsonProperty("message_text")
  private String messageText;
  @JsonProperty("read_status")
  private String readStatus;  //TODO readStatus = enum [SENT, READ]
}
