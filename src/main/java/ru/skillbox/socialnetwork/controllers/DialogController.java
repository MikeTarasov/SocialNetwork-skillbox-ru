package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.services.DialogService;

@RestController
@RequestMapping("/api/v1/dialogs")
public class DialogController {
    private final DialogService dialogService;

    @Autowired
    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    @PostMapping("/")
    public ResponseEntity<ErrorTimeDataResponse> getApiPost(@RequestBody ListUserIdsRequest listUserIdsRequest) {

        return ResponseEntity.ok(dialogService.createDialog(listUserIdsRequest.getUserIds()));
    }
}
