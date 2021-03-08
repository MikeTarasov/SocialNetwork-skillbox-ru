package ru.skillbox.socialnetwork.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entities.Notification;
import ru.skillbox.socialnetwork.model.entities.Person;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByPersonNotificationAndIsRead(Person person, int isRead, Pageable pageable);

    long countNotificationByPersonNotification(Person person);


    long countByPersonNotificationAndIsRead(Person person, int isRead);
}