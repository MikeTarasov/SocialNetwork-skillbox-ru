package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entity.PostComment;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CommentEntityResponse> getCommentEntityResponseList(
            List<PostComment> listComments) {
        return listComments.stream().map(lc -> CommentEntityResponse.builder()
                .parentId(lc.getParentId())
                .commentText(lc.getCommentText())
                .id(lc.getId())
                .postId(lc.getPost().getId())
                .time(lc.getTimestamp())
                .authorId(lc.getPerson().getId())
                .isBlocked(lc.getIsBlocked())
                .build())
                .collect(Collectors.toList());
    }

}
