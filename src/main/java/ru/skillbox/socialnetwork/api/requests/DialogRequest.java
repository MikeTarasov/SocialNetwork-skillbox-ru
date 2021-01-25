package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogRequest {
    @JsonProperty(defaultValue = "", required = false)
    private String query;
    @JsonProperty(defaultValue = "20", required = false)
    private int itemPerPage;
    @JsonProperty(defaultValue = "0", required = false)
    private int offset;

}
