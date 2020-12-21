package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.ItemIdTypeRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.LikesListUsersResponse;
import ru.skillbox.socialnetwork.api.responses.LikesResponse;

@RestController
public class LikeController {

  @GetMapping("/liked")
  public ResponseEntity<?> isUserHasLiked(
      @RequestParam(value = "person_id", required = false) int personId,
      @RequestParam(value = "item_id") int itemId,
      @RequestParam(value = "type") String type) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 121221, new LikesResponse()));
  }

  @GetMapping("/likes")
  public ResponseEntity<?> getListOfLikes(
      @RequestParam(value = "item_id") int itemId,
      @RequestParam(value = "type") String type) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123, new LikesListUsersResponse()));
  }

  @PutMapping("/likes")
  public ResponseEntity<?> putLike(@RequestBody ItemIdTypeRequest item) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 321111, new LikesListUsersResponse()));
  }

  @DeleteMapping("/likes")
  public ResponseEntity<?> deleteLike(
      @RequestParam(value = "item_id") int itemId,
      @RequestParam(value = "type") String type) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 32111244, new LikesResponse()));
  }


}
