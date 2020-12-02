package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD:src/main/java/ru/skillbox/socialnetwork/api/responses/ResponseError.java
import lombok.Data;
import org.springframework.http.ResponseEntity;
=======
>>>>>>> 06bbedb6cde8f2005973460fa8d99277c7d9701e:src/main/java/ru/skillbox/socialnetwork/api/responses/ErrorErrorDescriptionResponse.java
import ru.skillbox.socialnetwork.model.enums.ErrorDescriptions;


public class ErrorErrorDescriptionResponse {

  private String error;
  @JsonProperty("error_description")
  private String errorDescription;

  public ErrorErrorDescriptionResponse(ErrorDescriptions errorDescriptions) {
    error = errorDescriptions.toString().toLowerCase();
    errorDescription = errorDescriptions.getDescription();
  }

  public ResponseError(String error){
    this.error = error;
  }
}