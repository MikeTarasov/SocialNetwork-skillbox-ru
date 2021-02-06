package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.model.enums.FriendStatus;
import ru.skillbox.socialnetwork.services.FriendService;

import java.util.List;

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
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage){

        return ResponseEntity.ok(friendService.getFriends(name, offset, itemPerPage, FriendStatus.REQUEST));
    }

    @GetMapping("/friends/recommendations")
    public List recommendations(){
        return null;
    }
    @GetMapping("/is/friends")
    public boolean isExist(){
        return false;
    }

    @GetMapping("/friends")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getFriends(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.ok(friendService.getFriends(name, offset, itemPerPage, FriendStatus.FRIEND));
    }

    @PostMapping("/friends")
    public long add(){
        return 0L;
    }

    @DeleteMapping("/friends")
    public long delete(){
        return 0L;
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity get(@PathVariable int id){
        return null;
    }
}
