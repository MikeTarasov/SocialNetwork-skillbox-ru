package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParentIdCommentTextRequest {

  @JsonProperty("parent_id")
  private Long parenId;
  @JsonProperty("comment_text")
  private String commentText;

  public Long getParenId() {
    return parenId;
  }

  public void setParenId(Long parenId) {
    this.parenId = parenId;
  }

  public String getCommentText() {
    return commentText;
  }

  public void setCommentText(String commentText) {
    this.commentText = commentText;
  }
}