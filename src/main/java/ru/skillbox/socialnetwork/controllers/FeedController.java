package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.services.FeedService;

@RestController
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> feed(@RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "offset", defaultValue = "0") int offset,
                                  @RequestParam(name = "itemPerPage", defaultValue = "20") int itemPerPage) {

        return feedService.getFeed(name, offset, itemPerPage);

    }


}
