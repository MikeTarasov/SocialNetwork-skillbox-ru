package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.socialnetwork.services.NotificationsService;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;

    @Autowired
    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getApiNotifications(
            @RequestParam(name = "offset", defaultValue = "0", required = false) Integer offset,
            @RequestParam(defaultValue = "20", required = false) Integer itemPerPage) {
        return notificationsService.getApiNotifications(offset, itemPerPage);
    }

    @PutMapping("")
    public ResponseEntity<?> putApiNotifications(@RequestParam(value = "id", required = false) Long id,
                                                 @RequestParam(value = "all", required = false) Boolean all) {
        return notificationsService.putApiNotifications(id, all);
    }
}
