package ru.skillbox.socialnetwork.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.model.entity.NotificationType;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

  Optional<NotificationType> findByName(String name);

}
