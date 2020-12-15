package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;

@Service
public interface ProfileService {

    public ErrorTimeDataResponse getUser(long id);
    public ErrorTimeDataResponse getCurrentUser();
    public ErrorTimeDataResponse updateCurrentUser(PersonEditRequest personEditRequest);
    public ErrorTimeDataResponse deleteCurrentUser();
    public ErrorTimeDataResponse setBlockUserById(long id, int block);
}
