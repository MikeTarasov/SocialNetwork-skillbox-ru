package ru.skillbox.socialnetwork.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entities.NotificationType;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findByName(String name);
}