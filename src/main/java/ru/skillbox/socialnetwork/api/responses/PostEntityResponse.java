package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entities.Post;
import ru.skillbox.socialnetwork.repositories.PostCommentRepository;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostEntityResponse {

    private long id;
    private long time;
    private PersonEntityResponse author;
    private String title;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private boolean isBlocked;

    private int likes;
    private List<CommentEntityResponse> comments;
    private String type;

    public PostEntityResponse(Post post, PostCommentRepository postCommentRepository) {
        id = post.getId();
        time = post.getTimestamp();
        author = new PersonEntityResponse(post.getAuthor());
        title = post.getTitle();
        postText = post.getPostText();
        isBlocked = post.isBlocked();
        likes = post.getLikes().size();
        comments = CommentEntityResponse.getCommentEntityResponseList(post.getComments(), postCommentRepository);
    }

    public PostEntityResponse(Post post, PostCommentRepository postCommentRepository, String type) {
        this(post, postCommentRepository);
        this.type = type;
    }
}