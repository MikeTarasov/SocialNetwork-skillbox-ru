package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
