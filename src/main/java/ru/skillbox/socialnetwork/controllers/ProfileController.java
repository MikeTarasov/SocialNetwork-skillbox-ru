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
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        ErrorTimeDataResponse response = profileService.getCurrentUser();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody PersonEditRequest requestBody) {
        // TODO: verify at least one field in PersonEditRequest != null
        ErrorTimeDataResponse response = profileService.updateCurrentUser(requestBody);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser() {
        ErrorTimeDataResponse response = profileService.deleteCurrentUser();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ErrorTimeDataResponse> getUserById(@PathVariable("id") long id) {
        ErrorTimeDataResponse response = profileService.getUser(id);
        return ResponseEntity.ok(response);
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
             @RequestBody TitlePostTextRequest requestBody
             ) {
        return ResponseEntity.ok(profileService.putPostOnWall(id, publishDate, requestBody));
    }


    @GetMapping("/search")
    public ResponseEntity<ErrorTimeTotalOffsetPerPageListDataResponse> userSearch(
            @RequestParam(name = "first_name", required = false) String firstName,
            @RequestParam(name = "last_name", required = false) String lastName,
            @RequestParam(name = "age_from", required = false, defaultValue = "0") int ageFrom,
            @RequestParam(name = "age_to", required = false, defaultValue = "0") int ageTo,
            //@RequestParam(name = "country_id", required = false) int countryId,
            //@RequestParam(name = "city_id", required = false) int cityId,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "itemPerPage", required = false, defaultValue = "20") int itemPerPage) {

        return ResponseEntity.ok(profileService.search(
                firstName, lastName, ageFrom, ageTo,
                offset, itemPerPage
                ));
    }


    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUserById(@PathVariable("id") long id) {
        ErrorTimeDataResponse response = profileService.setBlockUserById(id, 1);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUserById(@PathVariable("id") long id) {
        ErrorTimeDataResponse response = profileService.setBlockUserById(id, 0);
        return ResponseEntity.ok(response);
    }
}