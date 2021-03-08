package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentIdCommentTextRequest {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_text")
    private String commentText;
}