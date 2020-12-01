package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResponseLongpollHistory {
    private List<ResponseMessage> messages;
    private List<ResponseProfile> profiles;
    @JsonProperty("count")
    private int messagesCount;

    public ResponseLongpollHistory (List<ResponseMessage> messages, List<ResponseProfile> profiles){
        this.profiles = profiles;
        this.messages= messages;
        messagesCount = messages.size();
    }
}
