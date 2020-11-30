package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseMessage {
    private int id;
    private long timestamp;
    @JsonProperty("author_id")
    private int authorId;
    @JsonProperty("recipient_id")
    private int recipientId;
    @JsonProperty("message_text")
    String messageText;
    @JsonProperty("read_status")
    String readStatus;
}
