package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
public class MessagesProfilesResponse { //TODO забыли использовать???

    private CountListMessagesResponse messages;
    private List<PersonEntityResponse> profiles;
}