package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;

@Service
public interface ProfileService {

    public ErrorTimeDataResponse getUser(int id);
}
