package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entities.PostComment;
import ru.skillbox.socialnetwork.repositories.PostCommentRepository;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @JsonProperty("author")
    private PersonEntityResponse author;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    @JsonProperty("sub_comments")
    private List<CommentEntityResponse> subComments;


    public CommentEntityResponse(PostComment comment, PostCommentRepository repository) {
        this.parentId = comment.getParentId();
        this.commentText = comment.getCommentText();
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.time = comment.getTimestamp();
        this.author = new PersonEntityResponse(comment.getPerson());
        this.isBlocked = comment.getIsBlocked();
        this.isDeleted = comment.getIsDeleted();
        subComments = new ArrayList<>();
        List<PostComment> subComments = repository.findByParentId(comment.getId());
        if (subComments.size() != 0 && parentId == null) addSubComment(subComments, repository);
    }

    public static List<CommentEntityResponse> getCommentEntityResponseList(
            List<PostComment> listComments, PostCommentRepository repository) {

        List<CommentEntityResponse> response = new ArrayList<>();

        listComments.stream().filter(comment -> comment.getParentId() == null)
                .forEach(comment -> response.add(new CommentEntityResponse(comment, repository)));

        return response;
    }

    private void addSubComment(List<PostComment> comments, PostCommentRepository repository) {

        comments.forEach(comment -> subComments.add(new CommentEntityResponse(comment, repository)));
    }
}