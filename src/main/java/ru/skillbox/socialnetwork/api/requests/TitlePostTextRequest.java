package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitlePostTextRequest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("post_text")
    private String postText;
}