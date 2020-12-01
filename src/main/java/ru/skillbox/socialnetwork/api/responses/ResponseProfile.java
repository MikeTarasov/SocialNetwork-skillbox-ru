package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)

public class ResponseProfile {
    private String error;
    private long timestamp;
    private ResponsePerson data;

    public ResponseProfile(String error, ResponsePerson data){
        this.error = error;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }

}
