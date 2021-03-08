package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.ItemIdTypeRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.LikesResponse;
import ru.skillbox.socialnetwork.services.PostLikeService;

@RestController
public class LikeController {

    private final PostLikeService postLikeService;

    public LikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @GetMapping("/liked")
    public ResponseEntity<?> isUserHasLiked(@RequestParam(value = "user_id", required = false) Long userId,
                                            @RequestParam(value = "item_id") long itemId,
                                            @RequestParam(value = "type") String type) {

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse(
                        new LikesResponse(postLikeService.isUserHasLiked(userId, itemId, type))));
    }

    @GetMapping("/likes")
    public ResponseEntity<?> getListOfLikes(@RequestParam(value = "item_id") long itemId,
                                            @RequestParam(value = "type") String type) {

        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse(postLikeService.getListOfLikes(itemId, type)));
    }

    @PutMapping("/likes")
    public ResponseEntity<?> putLike(@RequestBody ItemIdTypeRequest request) {

        return postLikeService.putLike(request);
    }

    @DeleteMapping("/likes")
    public ResponseEntity<?> deleteLike(@RequestParam(value = "item_id") long itemId,
                                        @RequestParam(value = "type") String type) {

        return postLikeService.deleteLike(itemId, type);
    }
}