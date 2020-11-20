package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.RequestAccountEmail;
import ru.skillbox.socialnetwork.api.requests.RequestAccountNotifications;
import ru.skillbox.socialnetwork.api.requests.RequestAccountPasswordSet;
import ru.skillbox.socialnetwork.api.requests.RequestAccountRegister;
import ru.skillbox.socialnetwork.api.responses.ResponseMessageData;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

  @PostMapping("/register")
  public ResponseEntity<?> postApiAccountRegister(
      @RequestBody RequestAccountRegister requestBody) {
    return ResponseEntity.status(200).body(new ResponseMessageData());
  }

  @PutMapping("/password/recovery")
  public ResponseEntity<?> putApiAccountPasswordRecovery(
      @RequestBody RequestAccountEmail requestBody) {
    return ResponseEntity.status(200).body(new ResponseMessageData());
  }

  @PutMapping("/password/set")
  public ResponseEntity<?> putApiAccountPasswordSet(
      @RequestBody RequestAccountPasswordSet requestBody) {
    return ResponseEntity.status(200).body(new ResponseMessageData());
  }

  @PutMapping("/email")
  public ResponseEntity<?> putApiAccountEmail(@RequestBody RequestAccountEmail requestBody) {
    return ResponseEntity.status(200).body(new ResponseMessageData());
  }

  @PutMapping("/notifications")
  public ResponseEntity<?> putApiAccountNotifications(
      @RequestBody RequestAccountNotifications requestBody) {
    return ResponseEntity.status(200).body(new ResponseMessageData());
  }
}