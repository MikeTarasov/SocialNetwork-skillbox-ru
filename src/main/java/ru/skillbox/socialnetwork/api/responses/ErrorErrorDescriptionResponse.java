package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.skillbox.socialnetwork.model.enums.ErrorDescriptions;


public class ErrorErrorDescriptionResponse {

  private String error;
  @JsonProperty("error_description")
  private String errorDescription;

  public ErrorErrorDescriptionResponse(ErrorDescriptions errorDescriptions) {
    error = errorDescriptions.toString().toLowerCase();
    errorDescription = errorDescriptions.getDescription();
  }
}