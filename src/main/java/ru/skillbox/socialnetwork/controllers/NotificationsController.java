package ru.skillbox.socialnetwork.controllers;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.services.NotificationsService;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;

    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getApiNotifications(@Param("offset") int offset, @Param("itemPerPage") int itemPerPage) {
        return notificationsService.getApiNotifications(offset, itemPerPage);
    }

    @PutMapping("")
    public ResponseEntity<?> putApiNotifications(@Param("id") long id, @Param("all") boolean all) {
        return notificationsService.putApiNotifications(id, all);
    }
}
