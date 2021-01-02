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

    private long id;
    @JsonProperty("parent_id")
    private long parentId;
    @JsonProperty("post_id")
    private long postId;
    private long time;
    @JsonProperty("author_id")
    private long authorId;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
