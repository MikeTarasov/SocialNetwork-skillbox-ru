package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Notification;
import ru.skillbox.socialnetwork.model.entity.Person;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByPersonNotificationAndIsRead(Person person, int isRead, Pageable pageable);

    long countNotificationByPersonNotification(Person person);
}
