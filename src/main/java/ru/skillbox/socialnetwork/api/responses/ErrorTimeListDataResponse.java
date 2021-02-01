package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ErrorTimeListDataResponse {

    private String error;
    private long timestamp;
    private List<?> data;

    public ErrorTimeListDataResponse(List<?> data) {
        this.error = "";
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }
}