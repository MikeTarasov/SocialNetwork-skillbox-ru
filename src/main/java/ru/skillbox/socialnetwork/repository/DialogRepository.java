package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {

    // method only for test purpose
    Optional<Dialog> findByOwner(Person person);

    Page<Dialog> findByIdIn(List<Long> dialogIdList, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} d SET d.unreadCount = d.unreadCount + 1 WHERE d.id = :id")
    void incrementUnreadCountById(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE #{#entityName} d SET d.unreadCount = 0 WHERE d.id = :id")
    void resetUnreadCountById(Long id);
}
