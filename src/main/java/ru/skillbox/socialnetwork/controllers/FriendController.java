package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.ListUserIdsRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.ListDataResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;
import ru.skillbox.socialnetwork.services.FriendService;

@RestController
public class FriendController {

    private final FriendService friendService;

    @Autowired
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/friends/request")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getRequests(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.ok(friendService.getFriends(name, offset, itemPerPage, FriendStatus.REQUEST));
    }

    @GetMapping("/friends/recommendations")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> recommendations(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.ok(friendService.getRecommendations(offset, itemPerPage));
    }

    @PostMapping("/is/friends")
    public ResponseEntity<ListDataResponse> isFriend(@RequestBody ListUserIdsRequest userIds) {

        return ResponseEntity.ok(new ListDataResponse(friendService.isFriend(userIds.getUserIds())));
    }

    @GetMapping("/friends")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getFriends(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.ok(friendService.getFriends(name, offset, itemPerPage, FriendStatus.FRIEND));
    }

    @PostMapping("/friends/{id}")
    public ResponseEntity<ErrorTimeDataResponse> add(@PathVariable Long id) {

        friendService.addFriend(id);
        return ResponseEntity.ok(new ErrorTimeDataResponse(new MessageResponse()));
    }

    @DeleteMapping("/friends/{id}")
    public ResponseEntity<ErrorTimeDataResponse> delete(@PathVariable Long id) {

        friendService.deleteFriend(id);
        return ResponseEntity.ok(new ErrorTimeDataResponse(new MessageResponse()));
    }
}