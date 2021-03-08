package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogRequest {

    @JsonProperty(defaultValue = "")
    private String query;

    @JsonProperty(defaultValue = "20")
    private int itemPerPage;

    @JsonProperty(defaultValue = "0")
    private int offset;
}