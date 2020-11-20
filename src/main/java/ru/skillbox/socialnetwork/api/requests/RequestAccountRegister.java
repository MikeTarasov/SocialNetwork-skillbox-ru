package ru.skillbox.socialnetwork.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAccountRegister {

  private String email;
  private String passwd1;
  private String passwd2;
  private String firstName;
  private String lastName;
  private String code;
}