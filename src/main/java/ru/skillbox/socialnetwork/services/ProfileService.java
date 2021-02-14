package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;

@Service
public interface ProfileService {

    ErrorTimeDataResponse getUser(long id);

    ErrorTimeDataResponse getCurrentUser();

    ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest);

    ErrorTimeDataResponse deleteCurrentUser();

    ErrorTimeDataResponse setBlockUserById(long id, int block);

    ErrorTimeTotalOffsetPerPageListDataResponse getWallPosts(long userId, int offset, int itemPerPage);

    ErrorTimeDataResponse putPostOnWall(long id, Long publishDate, TitlePostTextRequest requestBody);

    ErrorTimeTotalOffsetPerPageListDataResponse search(
            String firstName, String lastName, String city, String country, Integer ageFrom, Integer ageTo,
            Integer offset, Integer itemPerPage);
}
