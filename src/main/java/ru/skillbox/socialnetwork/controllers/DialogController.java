package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.DialogRequest;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
import ru.skillbox.socialnetwork.api.requests.MessageTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.services.DialogService;

@RestController
@RequestMapping("/dialogs")
public class DialogController {
    private final DialogService dialogService;

    @Autowired
    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @GetMapping("")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getDialogList(
            @RequestBody(required = false) DialogRequest dialogRequest) {

        return ResponseEntity.ok(dialogService.getDialogsLastMessages(dialogRequest));
    }


    @PostMapping("")
    public ResponseEntity<ErrorTimeDataResponse> getApiPost(@RequestBody ListUserIdsRequest listUserIdsRequest) {

        return ResponseEntity.ok(dialogService.createDialog(listUserIdsRequest.getUserIds()));
    }


    @PutMapping("/{id}/users")
    public ResponseEntity<ErrorTimeDataResponse> addUserToDialog(
            @PathVariable Long id, @RequestBody ListUserIdsRequest listUserIdsRequest) {

        return ResponseEntity.ok(dialogService.addUsersToDialog(id, listUserIdsRequest.getUserIds()));
    }


    @DeleteMapping("/{id}/users")
    public ResponseEntity<ErrorTimeDataResponse> deleteUsersFromDialog(
            @PathVariable Long id, @RequestBody ListUserIdsRequest listUserIdsRequest) {

        return ResponseEntity.ok(dialogService.deleteUsersFromDialog(id, listUserIdsRequest.getUserIds()));
    }


    @GetMapping("/{id}/users/invite")
    public ResponseEntity<ErrorTimeDataResponse> getInviteLink(@PathVariable Long id) {

        return ResponseEntity.ok(dialogService.getInviteLink(id));
    }


    @PutMapping("/{id}/users/join")
    public ResponseEntity<ErrorTimeDataResponse> joinByInvite(@PathVariable Long id, @RequestBody LinkRequest link) {

        return ResponseEntity.ok(dialogService.joinByInvite(id, link));
    }

    @GetMapping("/{id}/activity/{user_id}")
    public ResponseEntity<ErrorTimeDataResponse> getPersonActivity(
            @PathVariable Long id, @PathVariable(name = "user_id") Long PersonId) {

        return ResponseEntity.ok(dialogService.getPersonStatus(id, PersonId));
    }


    @PostMapping("/{id}/activity/{user_id}")
    public ResponseEntity<ErrorTimeDataResponse> setPersonActivity(
            @PathVariable Long id, @PathVariable(name = "user_id") Long PersonId) {

        return ResponseEntity.ok(dialogService.setPersonStatus(id, PersonId));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getListMessages(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {

        return ResponseEntity.ok(dialogService.getMessagesByDialogId(id, query, offset, limit));
    }

    @PostMapping("/{dialogId}/messages")
    public ResponseEntity<ErrorTimeDataResponse> sendMessages(@PathVariable Long dialogId,
                                                              @RequestBody MessageTextRequest messageTextRequest) {

        return ResponseEntity.ok(dialogService.sendMessage(dialogId, messageTextRequest));
    }


    @DeleteMapping("/{dialogId}/messages/{messageId}")
    public ResponseEntity<ErrorTimeDataResponse> deleteMessage(
            @PathVariable Long dialogId, @PathVariable Long messageId) {

        return ResponseEntity.ok(dialogService.deleteMessage(dialogId, messageId));
    }


    @PutMapping("/{dialogId}/messages/{messageId}")
    public ResponseEntity<ErrorTimeDataResponse> changeMessage(@PathVariable Long dialogId,
                                                               @PathVariable Long messageId,
                                                               @RequestBody MessageTextRequest messageTextRequest) {

        return ResponseEntity.ok(dialogService.changeMessage(dialogId, messageId, messageTextRequest));
    }


    @PutMapping("/{dialogId}/messages/{messageId}/recover")
    public ResponseEntity<ErrorTimeDataResponse> recoverMessage(@PathVariable Long dialogId,
                                                               @PathVariable Long messageId) {

        return ResponseEntity.ok(dialogService.recoverMessage(dialogId, messageId));
    }


    @PutMapping("/{dialogId}/messages/{messageId}/read")
    public ResponseEntity<ErrorTimeDataResponse> markReadMessage(@PathVariable Long dialogId,
                                                               @PathVariable Long messageId) {

        return ResponseEntity.ok(dialogService.markReadMessage(dialogId, messageId));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorTimeDataResponse> deleteDialog(@PathVariable Long id) {

        return ResponseEntity.ok(dialogService.deleteDialog(id));
    }

    @GetMapping("/unreaded")
    public ResponseEntity<ErrorTimeDataResponse> getNewMessagesCount() {

        return ResponseEntity.ok(dialogService.getNewMessagesCount());
    }
}
