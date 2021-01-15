package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;

import java.util.List;

@Service
public interface DialogService {

    public ErrorTimeDataResponse createDialog(List<Long> userIds);
}
