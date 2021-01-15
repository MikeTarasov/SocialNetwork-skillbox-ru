package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PersonToDialog;

import java.util.Optional;

public interface PersonToDialogRepository extends JpaRepository<PersonToDialog, Long> {
    Optional<PersonToDialog> findByPerson(Person Id);
}
