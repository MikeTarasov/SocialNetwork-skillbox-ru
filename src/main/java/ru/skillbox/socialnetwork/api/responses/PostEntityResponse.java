package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}