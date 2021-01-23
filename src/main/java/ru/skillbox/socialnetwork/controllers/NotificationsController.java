package ru.skillbox.socialnetwork.controllers;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private final NotificationsController notificationsController;

    public NotificationsController(NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    @GetMapping("")
    public ResponseEntity<?> getApiNotifications(@Param("offset") int offset, @Param("itemPerPage") int itemPerPage) {
        return notificationsController.getApiNotifications(offset, itemPerPage);
    }

    @PutMapping("")
    public ResponseEntity<?> putApiNotifications(@Param("id") long id, @Param("all") boolean all) {
        return notificationsController.putApiNotifications(id, all);
    }
}
