package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Dialog;
import ru.skillbox.socialnetwork.model.entity.Person;

import java.util.Optional;

@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {

    Optional<Dialog> findByOwner(Person person);
}
