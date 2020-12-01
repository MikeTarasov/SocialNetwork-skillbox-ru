package ru.skillbox.socialnetwork.api.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class LikesListUsersResponse {

  private String likes;
  private List<String> users;
}