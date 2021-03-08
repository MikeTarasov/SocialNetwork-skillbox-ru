package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorErrorDescriptionResponse {

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    public ErrorErrorDescriptionResponse(String errorDescription) {
        error = "invalid_request";
        this.errorDescription = errorDescription;
    }
}