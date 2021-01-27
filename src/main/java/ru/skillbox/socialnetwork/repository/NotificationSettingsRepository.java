package ru.skillbox.socialnetwork.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.NotificationSettings;
import ru.skillbox.socialnetwork.model.entity.NotificationType;
import ru.skillbox.socialnetwork.model.entity.Person;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

  Optional<NotificationSettings> findByPersonNSAndNotificationType(Person personNS,
      NotificationType notificationType);

}
