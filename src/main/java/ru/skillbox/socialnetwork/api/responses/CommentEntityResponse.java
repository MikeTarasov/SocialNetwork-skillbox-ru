package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntityResponse {

    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("comment_text")
    private String commentText;
    private long id;
    @JsonProperty("post_id")
    private long postId;
    private long time;
    @JsonProperty("author_id")
    private long authorId;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
