package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCityCountry {

  private int id;
  private String title;
}
