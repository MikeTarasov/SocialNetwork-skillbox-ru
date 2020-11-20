package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ResponseDataLikes {

  private String likes;
  private List<String> users;

  public ResponseDataLikes(String likes, List<String> users) {
    this.likes = likes;
    this.users = users;
  }

  // Delete api/likes
  public ResponseDataLikes(String likes) {
    this.likes = likes;
  }
}
