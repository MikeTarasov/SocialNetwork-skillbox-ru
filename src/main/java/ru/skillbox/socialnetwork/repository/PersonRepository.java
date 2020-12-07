package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  Person findByEmail(String email);

}
