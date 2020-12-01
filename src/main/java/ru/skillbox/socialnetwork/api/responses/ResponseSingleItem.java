package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
// universal response to wrap various specific responses with error message & timestamps
public class ResponseSingleItem {
    private String error;
    private long timestamp;
    private Object data;

    public ResponseSingleItem(String error, Object data) {
        this.error = error;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }
}
