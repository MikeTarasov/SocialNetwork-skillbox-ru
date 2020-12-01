package ru.skillbox.socialnetwork.api.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
public class MessagesProfilesResponse {

  private CountListMessagesResponse messages;
  private List<PersonEntityResponse> profiles;
}