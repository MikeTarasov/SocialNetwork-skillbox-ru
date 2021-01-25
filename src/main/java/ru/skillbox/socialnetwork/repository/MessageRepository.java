package ru.skillbox.socialnetwork.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.skillbox.socialnetwork.model.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = "SELECT * " +
            "FROM message ms " +
            "WHERE ms.dialog_id = :dialog_id AND ms.is_deleted = 0 AND ms.text LIKE CONCAT ('%',:query,'%') ORDER BY `time` DESC",
            nativeQuery = true)
    Page<Message> findMessageWithQueryWithPagination(@Param("query") String query,
                                                     @Param("dialog_id") Long dialogId,
                                                     Pageable pageable);
}
