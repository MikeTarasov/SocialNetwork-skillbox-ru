package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;

@Service
public interface FriendService {

    public ErrorTimeTotalOffsetPerPageListDataResponse getFriends(String name, Integer offset, Integer itemPerPage, FriendStatus friendStatus);

    public void addFriend(Long dstPersonId);
}

