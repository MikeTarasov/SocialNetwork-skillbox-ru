package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.model.entity.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
}
