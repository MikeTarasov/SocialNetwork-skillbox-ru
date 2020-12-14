package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entity.Person;


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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public PersonEntityResponse getAuthor() {
    return author;
  }

  public void setAuthor(PersonEntityResponse author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPostText() {
    return postText;
  }

  public void setPostText(String postText) {
    this.postText = postText;
  }

  public boolean isBlocked() {
    return isBlocked;
  }

  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  public List<CommentEntityResponse> getComments() {
    return comments;
  }

  public void setComments(List<CommentEntityResponse> comments) {
    this.comments = comments;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}