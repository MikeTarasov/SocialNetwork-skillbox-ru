package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.services.StorageService;

@RestController
public class StorageController {

  private final StorageService storageService;

  public StorageController(StorageService storageService) {
    this.storageService = storageService;
  }

  @PostMapping("/storage")
  public ResponseEntity<?> uploadFile(@RequestParam(value = "type") String pathToFile) {
    return storageService.getUpload(pathToFile);
  }

}
