package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;

@Service
public interface ProfileService {

    public ErrorTimeDataResponse getUser(long id);
    public ErrorTimeDataResponse getCurrentUser();
    public ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest);
    public ErrorTimeDataResponse deleteCurrentUser();
    public ErrorTimeDataResponse setBlockUserById(long id, int block);
    public ErrorTimeTotalOffsetPerPageListDataResponse getWallPosts(long userId, int offset, int itemPerPage);
    public ErrorTimeDataResponse putPostOnWall(long id, Long publishDate, TitlePostTextRequest requestBody);
    public ErrorTimeTotalOffsetPerPageListDataResponse search(String firstName, String lastName, int ageFrom, int ageTo, int offset, int itemPerPage);
}
