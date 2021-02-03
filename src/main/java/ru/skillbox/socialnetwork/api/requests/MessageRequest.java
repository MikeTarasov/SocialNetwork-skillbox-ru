package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.enums.ReadStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
   private Long id;
   private Long time;
   @JsonProperty("author_id")
   private Long authorId;
   @JsonProperty("recipient_id")
   private Long recipientId;
   @JsonProperty("message_text")
   private String messageText;
   @JsonProperty("read_status")
   private ReadStatus readStatus;

}
