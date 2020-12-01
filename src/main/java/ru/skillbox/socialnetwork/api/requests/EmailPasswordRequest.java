package ru.skillbox.socialnetwork.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailPasswordRequest {

  private String email;
  private String password;
}
