package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class CommentEntityResponse {

    private int id;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("post_id")
    private String postId;
    @JsonProperty("author_id")
    private int authorId;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private boolean isBlocked;
}
