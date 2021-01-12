package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class OnlineLastActivityResponse {

  private boolean online;
  @JsonProperty("last_activity")
  private long lastActivity;
}