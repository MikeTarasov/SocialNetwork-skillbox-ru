package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.EmailPassPassFirstNameLastNameCodeRequest;
import ru.skillbox.socialnetwork.api.requests.EmailRequest;
import ru.skillbox.socialnetwork.api.requests.NotificationTypeEnableRequest;
import ru.skillbox.socialnetwork.api.requests.TokenPasswordRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;

@RestController
@RequestMapping("/account")
public class AccountController {

  @PostMapping("/register")
  public ResponseEntity<?> postApiAccountRegister(
      @RequestBody EmailPassPassFirstNameLastNameCodeRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse()));
  }


  @PutMapping("/password/recovery")
  public ResponseEntity<?> putApiAccountPasswordRecovery(
      @RequestBody EmailRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse()));
  }


  @PutMapping("/password/set")
  public ResponseEntity<?> putApiAccountPasswordSet(
      @RequestBody TokenPasswordRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse()));
  }


  @PutMapping("/email")
  public ResponseEntity<?> putApiAccountEmail(@RequestBody EmailRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse()));
  }


  @PutMapping("/notifications")
  public ResponseEntity<?> putApiAccountNotifications(
      @RequestBody NotificationTypeEnableRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse()));
  }
}