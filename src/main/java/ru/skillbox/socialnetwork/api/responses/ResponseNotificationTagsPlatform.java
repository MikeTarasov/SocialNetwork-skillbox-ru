package ru.skillbox.socialnetwork.api.responses;

import java.util.List;
import lombok.Data;

@Data
public class ResponseNotificationTagsPlatform {

  private String error;
  private long timestamp;
  private int total;
  private int offset;
  private int perPage;
  private List<?> data;

  // data:
  // Notification -> ResponseNotificationBase
  // Tags -> ResponseTag
  // Platform -> ResponseCityCountry
  public ResponseNotificationTagsPlatform(String error, int total, int offset, int perPage,
      List<?> data) {
    this.error = error;
    this.timestamp = System.currentTimeMillis();
    this.total = total;
    this.offset = offset;
    this.perPage = perPage;
    this.data = data;
  }
}