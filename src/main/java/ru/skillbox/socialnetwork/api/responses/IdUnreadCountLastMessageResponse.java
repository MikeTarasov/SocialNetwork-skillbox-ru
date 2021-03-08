package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdUnreadCountLastMessageResponse {

    @JsonProperty("id")
    private long id;

    @JsonProperty("unread_count")
    private int unreadCount;

    @JsonProperty("last_message")
    private MessageEntityResponse lastMessage;
}