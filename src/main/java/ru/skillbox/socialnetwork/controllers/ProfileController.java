package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class ProfileController {

    //service

    //constructor ProfileController(Service service)

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return null;
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody int requestBody) {
        return null;
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@RequestParam int id) {
        return null;
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<?> getNotesOnUserWall(@RequestParam int id) {
        return null;
    }

    @PostMapping("/{id}/wall")
    public ResponseEntity<?> postNoteOnUserWall(@RequestParam int id, @RequestBody int requestBody) {
        return null;
    }

    @GetMapping("/search")
    public ResponseEntity<?> userSearch(@RequestBody int requestBody) {
        return null;
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUserById(@RequestParam int id) {
        return null;
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUserById(@RequestParam int id) {
        return null;
    }

}
