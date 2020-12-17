package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParentIdCommentTextRequest {

  @JsonProperty("parent_id")
  private Integer parenId;
  @JsonProperty("comment_text")
  private String commentText;

  public Integer getParenId() {
    return parenId;
  }

  public void setParenId(Integer parenId) {
    this.parenId = parenId;
  }

  public String getCommentText() {
    return commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }
}