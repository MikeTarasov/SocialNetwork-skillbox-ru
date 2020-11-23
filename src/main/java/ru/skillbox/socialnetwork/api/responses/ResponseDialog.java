package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ResponseDialog {
    private int id;
    @JsonProperty("unread_count")
    private int unreadCount;
    ResponseMessage last_message;
}
