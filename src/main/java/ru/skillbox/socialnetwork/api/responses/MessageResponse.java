package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class MessageResponse {

  private String message;

  public MessageResponse() {
    message = "ok";
  }
}