package ru.skillbox.socialnetwork.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemIdTypeRequest {

  @JsonProperty("item_id")
  private int itemId;
  private String type; //TODO type = enum [ Post, Comment ]
}