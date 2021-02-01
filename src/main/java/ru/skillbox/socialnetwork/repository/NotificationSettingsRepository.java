package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.NotificationSettings;
import ru.skillbox.socialnetwork.model.entity.NotificationType;
import ru.skillbox.socialnetwork.model.entity.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByPersonNSAndNotificationTypeId(Person person, long notificationTypeId);

    NotificationSettings findByPersonNSAndNotificationType(Person person, NotificationType notificationType);

    List<NotificationSettings> findByPersonNS(Person person);

    List<NotificationSettings> findByPersonNSAndEnable(Person person, int isEnable);
}
