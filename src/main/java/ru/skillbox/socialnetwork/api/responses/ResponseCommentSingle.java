package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)


public class ResponseCommentSingle {
    private String error;
    private long timestamp;
    private ResponseComment data;

    public ResponseCommentSingle(String error, ResponseComment data){
        this.error = error;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }
}
