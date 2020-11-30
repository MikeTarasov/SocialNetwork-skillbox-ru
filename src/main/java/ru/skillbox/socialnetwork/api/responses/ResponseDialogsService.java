package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
// united some dialog-specific responses for dialogs
public class ResponseDialogsService {
    private String link;
    @JsonProperty("message_id")
    private int messageId;
    @JsonProperty("user_ids")
    private List<Integer> userIds;

    public ResponseDialogsService(String link) {
        this.link = link;
    }

    public ResponseDialogsService(int messageId) {
        this.messageId = messageId;
    }

    public ResponseDialogsService(List<Integer> userIds) {
        this.userIds = userIds;
    }
}
