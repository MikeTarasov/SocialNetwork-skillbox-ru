package ru.skillbox.socialnetwork.api.responses;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseFriendsDataList {

  private List<ResponseDataFriends> data;
}
