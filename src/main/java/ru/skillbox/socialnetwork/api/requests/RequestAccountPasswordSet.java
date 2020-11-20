package ru.skillbox.socialnetwork.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAccountPasswordSet {

  private String token;
  private String password;
}