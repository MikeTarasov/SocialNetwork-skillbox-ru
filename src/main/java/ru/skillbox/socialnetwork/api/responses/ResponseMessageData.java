package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ResponseMessageData {

  private String error = "";
  private long timestamp;
  private Object data;


  // account + auth message = ok
  public ResponseMessageData() {
    timestamp = System.currentTimeMillis();
    data = new ResponseData("ok");
  }

  // account + auth { message != ok & error != "" }
  public ResponseMessageData(String error, String message) {
    this.error = error;
    timestamp = System.currentTimeMillis();
    data = new ResponseData(message);
  }

  // Get/api/liked
  public ResponseMessageData(boolean likes) {
    timestamp = System.currentTimeMillis();
    data = new ResponseData(likes);
  }

  // Get/api/likes
  public ResponseMessageData(String likes, List<String> users) {
    timestamp = System.currentTimeMillis();
    data = new ResponseDataLikes(likes, users);
  }

  // Delete api/likes
  public ResponseMessageData(String likes) {
    timestamp = System.currentTimeMillis();
    data = new ResponseDataLikes(likes);
  }
}