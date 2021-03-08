package ru.skillbox.socialnetwork.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnetwork.model.entities.Message;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT * " +
            "FROM message ms " +
            "WHERE ms.dialog_id = :dialog_id AND ms.is_deleted = 0 AND ms.message_text LIKE CONCAT ('%',:query,'%') ORDER BY ms.time DESC",
            nativeQuery = true)
    Page<Message> findMessageWithQueryWithPagination(@Param("query") String query,
                                                     @Param("dialog_id") Long dialogId,
                                                     Pageable pageable);
    // latest message in dialog
    Optional<Message> findTopByDialogIdOrderByTimeDesc(@Param("dialog_id") Long dialogId);
}
