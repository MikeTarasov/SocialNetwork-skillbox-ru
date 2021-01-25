package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.DialogRequest;
import ru.skillbox.socialnetwork.api.requests.LinkRequest;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
import ru.skillbox.socialnetwork.api.requests.MessageRequest;
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
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getDialogList(@RequestBody(required = false) DialogRequest dialogRequest) {
        if (dialogRequest != null) {
            return ResponseEntity.ok(dialogService.getDialogsList(dialogRequest));
        } else {
            return ResponseEntity.ok(dialogService.getDialogsList());
        }
    }


    @PostMapping("")
    public ResponseEntity<ErrorTimeDataResponse> getApiPost(@RequestBody ListUserIdsRequest listUserIdsRequest) {

        return ResponseEntity.ok(dialogService.createDialog(listUserIdsRequest.getUserIds()));
    }


    @PutMapping("/{id}/users")
    public ResponseEntity<ErrorTimeDataResponse> addUserToDialog(@PathVariable Long id,
                                                                 @RequestBody ListUserIdsRequest listUserIdsRequest) {
        return ResponseEntity.ok(dialogService.addUsersToDialog(id, listUserIdsRequest.getUserIds()));
    }

    @DeleteMapping("/{id}/users")
    public ResponseEntity<ErrorTimeDataResponse> deleteUsersFromDialog(@PathVariable Long id,
                                                                       @RequestBody ListUserIdsRequest listUserIdsRequest) {
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
    public ResponseEntity<ErrorTimeDataResponse> getPersonActivity(@PathVariable Long id, @PathVariable(name = "user_id") Long PersonId) {
        return ResponseEntity.ok(dialogService.getPersonStatus(id, PersonId));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ErrorTimeDataResponse> getListMessages(@PathVariable Long id,
                                                                 @RequestParam(required = false,defaultValue = "")String query,
                                                                 @RequestParam(required=false, defaultValue = "0") Integer offset,
                                                                 @RequestParam(required=false, defaultValue = "20") Integer limit){
        return ResponseEntity.ok(dialogService.getMessagesById(id, query, offset, limit));
    }
    @PostMapping("/{id}/messages")
    public ResponseEntity<ErrorTimeDataResponse> sendMessages(@PathVariable Long id,
                                                              @RequestBody MessageRequest messageRequest){
       return ResponseEntity.ok(dialogService.sendMessage(id,messageRequest));
    }
}
