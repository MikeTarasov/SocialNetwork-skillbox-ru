package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.services.ProfileService;

import java.util.ArrayList;

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
    public ResponseEntity<ErrorTimeDataResponse> getUserById(@PathVariable("id") int id) {
        ErrorTimeDataResponse response = profileService.getUser(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}/wall")
    public ResponseEntity<?> getNotesOnUserWall(@PathVariable("id") int id,
        @Param("offset") int offset, @Param("itemPerPage") int itemPerPage) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                123456,
                123,
                0,
                20,
                new ArrayList<PostEntityResponse>()
            ));
    }


    @PostMapping("/{id}/wall")
    public ResponseEntity<?> postNoteOnUserWall(@PathVariable("id") int id,
        @Param("publish_date") long publishDate, @RequestBody TitlePostTextRequest requestBody) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new PostEntityResponse()));
    }


    @GetMapping("/search")
    public ResponseEntity<?> userSearch(@Param("first_name") String firstName,
        @Param("last_name") String lastName, @Param("age_from") int ageFrom,
        @Param("age_to") int ageTo, @Param("country_id") int countryId,
        @Param("city_id") int cityId, @Param("offset") int offset,
        @Param("itemPerPage") int itemPerPage) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                "",
                123456,
                123,
                0,
                20,
                new ArrayList<PersonEntityResponse>()
            ));
    }


    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockUserById(@PathVariable("id") int id) {
        ErrorTimeDataResponse response = profileService.setBlockUserById(id, 1);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUserById(@PathVariable("id") int id) {
        ErrorTimeDataResponse response = profileService.setBlockUserById(id, 0);
        return ResponseEntity.ok(response);
    }
}