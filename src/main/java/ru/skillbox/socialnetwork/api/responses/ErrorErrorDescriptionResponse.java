package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.enums.ErrorDescriptions;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorErrorDescriptionResponse {

  private String error;
  @JsonProperty("error_description")
  private String errorDescription;

  public ErrorErrorDescriptionResponse(ErrorDescriptions errorDescriptions) {
    error = errorDescriptions.toString().toLowerCase();
    errorDescription = errorDescriptions.getDescription();
  }

  public ErrorErrorDescriptionResponse(String error) {
    this.error = error;
  }
}