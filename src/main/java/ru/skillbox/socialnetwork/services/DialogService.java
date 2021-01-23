package ru.skillbox.socialnetwork.services;

import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;

import java.util.List;

@Service
public interface DialogService {

    public ErrorTimeDataResponse createDialog(List<Long> userIds);
    public ErrorTimeTotalOffsetPerPageListDataResponse getDialogsList(String query, Integer offset, Integer itemPerPage);
    public ErrorTimeDataResponse addUsersToDialog(Long dialogId, List<Long> userIds);
    public ErrorTimeDataResponse deleteUsersFromDialog(Long dialogId, List<Long> userIds);
    public ErrorTimeDataResponse getInviteLink(Long dialogId);
    public ErrorTimeDataResponse joinByInvite(Long dialogId, LinkRequest inviteLink);

}
