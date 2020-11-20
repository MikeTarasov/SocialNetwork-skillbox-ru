package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ResponseAuthLoginAndTagsAndStorage {

  private String error;
  private long timestamp;
  private Object data;

  // api/auth/login
  public ResponseAuthLoginAndTagsAndStorage(String error, ResponsePerson data) {
    this.error = error;
    this.timestamp = System.currentTimeMillis();
    this.data = data;
  }

  // post api/tags
  public ResponseAuthLoginAndTagsAndStorage(String error, ResponseTag dataTag) {
    this.error = error;
    this.timestamp = System.currentTimeMillis();
    this.data = dataTag;
  }
}
