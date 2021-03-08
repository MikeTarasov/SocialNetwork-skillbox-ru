package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ErrorTimeDataResponse {

    private String error;
    private long timestamp;
    private Object data;

    public ErrorTimeDataResponse(String error, Object data) {
        this.error = error;
        timestamp = System.currentTimeMillis();
        this.data = data;
    }

    public ErrorTimeDataResponse(Object data) {
        this("", data);
    }
}