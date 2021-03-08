package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.services.ProfileService;

@RestController
@RequestMapping("/users")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {

        return ResponseEntity.ok(profileService.getCurrentUser());
    }


    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody PersonEditRequest requestBody) {

        return ResponseEntity.ok(profileService.updateCurrentUser(requestBody));
    }


    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser() {

        return ResponseEntity.ok(profileService.deleteCurrentUser());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ErrorTimeDataResponse> getUserById(@PathVariable("id") long id) {

        return ResponseEntity.ok(profileService.getUser(id));
    }


    @GetMapping("/{id}/wall")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> getNotesOnUserWall(
            @PathVariable("id") long id,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", required = false, defaultValue = "20") int itemPerPage) {

        return ResponseEntity.ok(profileService.getWallPosts(id, offset, itemPerPage));
    }


    @PostMapping("/{id}/wall")
    public ResponseEntity<ErrorTimeDataResponse> postNoteOnUserWall
            (@PathVariable("id") long id,
             @RequestParam(name = "publish_date", required = false) Long publishDate,
             @RequestBody TitlePostTextRequest requestBody) {

        return ResponseEntity.ok(profileService.putPostOnWall(id, publishDate, requestBody));
    }


    @GetMapping("/search")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> userSearch(
            @RequestParam(name = "first_name", required = false) String firstName,
            @RequestParam(name = "last_name", required = false) String lastName,
            @RequestParam(name = "age_from", required = false, defaultValue = "0") Integer ageFrom,
            @RequestParam(name = "age_to", required = false, defaultValue = "0") Integer ageTo,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = "itemPerPage", required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.status(200)
                .body(profileService.search(firstName, lastName, city, country, ageFrom, ageTo, offset, itemPerPage));
    }


    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUserById(@PathVariable("id") long id) {

        return ResponseEntity.ok(profileService.setBlockUserById(id, 1));
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUserById(@PathVariable("id") long id) {

        return ResponseEntity.ok(profileService.setBlockUserById(id, 0));
    }
}