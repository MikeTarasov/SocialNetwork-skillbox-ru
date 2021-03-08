package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
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
  private String type; //TODO type = enum[ POSTED, QUEUED ]

  public PostEntityResponse(long id, long time, PersonEntityResponse author, String title, String postText,
                            boolean isBlocked, int likes, List<CommentEntityResponse> comments) {
    this.id = id;
    this.time = time;
    this.author = author;
    this.title = title;
    this.postText = postText;
    this.isBlocked = isBlocked;
    this.likes = likes;
    this.comments = comments;
  }
}