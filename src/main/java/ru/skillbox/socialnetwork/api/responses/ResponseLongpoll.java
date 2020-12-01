package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor


public class ResponseLongpoll {
    private String key;
    private String server;
    private long ts;
}
