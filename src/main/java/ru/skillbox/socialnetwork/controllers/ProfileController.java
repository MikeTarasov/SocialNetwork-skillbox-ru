package ru.skillbox.socialnetwork.controllers;

import java.util.ArrayList;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.PersonEditRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;

@RestController
@RequestMapping("/users")
public class ProfileController {

    //service

    //constructor ProfileController(Service service)

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new PersonEntityResponse()));
    }


    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody PersonEditRequest requestBody) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new PersonEntityResponse()));
    }


    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser() {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new MessageResponse()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new PersonEntityResponse()));
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
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new MessageResponse()));
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<?> unblockUserById(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
            .body(new ErrorTimeDataResponse("", 123, new MessageResponse()));
    }
}