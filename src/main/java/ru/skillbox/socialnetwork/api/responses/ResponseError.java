package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnetwork.model.enums.ErrorDescriptions;

@Data
public class ResponseError {

  private String error;
  @JsonProperty("error_description")
  private String errorDescription;

  public ResponseError(ErrorDescriptions errorDescriptions) {
    error = errorDescriptions.toString().toLowerCase();
    errorDescription = errorDescriptions.getDescription();
  }

  public ResponseError(String error){
    this.error = error;
  }
}