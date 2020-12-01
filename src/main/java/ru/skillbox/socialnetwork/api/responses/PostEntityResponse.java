package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class PostEntityResponse {

  private int id;
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