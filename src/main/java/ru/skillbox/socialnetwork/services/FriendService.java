package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.UserIdStatusResponse;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;

import java.util.List;

@Service
public interface FriendService {

    public ErrorTimeTotalOffsetPerPageListDataResponse getFriends(String name, Integer offset, Integer itemPerPage, FriendStatus friendStatus);

    public void addFriend(Long dstPersonId);

    public void  deleteFriend(Long dstPersonId);

    public List<UserIdStatusResponse> isFriend(List<Long> userIds);

    public ErrorTimeTotalOffsetPerPageListDataResponse getRecommendations(Integer offset, Integer itemPerPage);
}

