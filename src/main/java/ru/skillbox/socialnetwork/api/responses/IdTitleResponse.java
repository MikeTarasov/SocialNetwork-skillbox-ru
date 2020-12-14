package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



@NoArgsConstructor
public class IdTitleResponse {

  private int id;
  private String title;

  public IdTitleResponse(String title) {
    this.title = title;
    this.id = title.hashCode();
  }

}