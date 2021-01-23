package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.model.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
