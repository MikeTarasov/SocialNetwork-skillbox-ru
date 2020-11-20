package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ResponseData {

  private String message;
  private boolean likes;

  public ResponseData(String message) {
    this.message = message;
  }

  public ResponseData(boolean likes) {
    this.likes = likes;
  }
}