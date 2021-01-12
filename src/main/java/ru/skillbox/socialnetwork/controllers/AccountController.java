package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.EmailPassPassFirstNameLastNameCodeRequest;
import ru.skillbox.socialnetwork.api.requests.EmailRequest;
import ru.skillbox.socialnetwork.api.requests.NotificationTypeEnableRequest;
import ru.skillbox.socialnetwork.api.requests.TokenPasswordRequest;
import ru.skillbox.socialnetwork.services.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> postApiAccountRegister(
            @RequestBody EmailPassPassFirstNameLastNameCodeRequest requestBody) {

        return accountService.postApiAccountRegister(requestBody);
    }


    @PutMapping("/password/recovery")
    public ResponseEntity<?> putApiAccountPasswordRecovery(
            @RequestBody EmailRequest requestBody) {

        return accountService.putApiAccountPasswordRecovery(requestBody);
    }


    @PutMapping("/password/set")
    public ResponseEntity<?> putApiAccountPasswordSet(@RequestBody TokenPasswordRequest requestBody) {

        return accountService.putApiAccountPasswordSet(requestBody);
    }


    @PutMapping("/email")
    public ResponseEntity<?> putApiAccountEmail(@RequestBody EmailRequest requestBody) {

        return accountService.putApiAccountEmail(requestBody);
    }


    @PutMapping("/notifications")
    public ResponseEntity<?> putApiAccountNotifications(
            @RequestBody NotificationTypeEnableRequest requestBody) {

        return accountService.putApiAccountNotifications(requestBody);
    }
}