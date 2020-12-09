package ru.skillbox.socialnetwork.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {

  Optional<Person> findByEmail(String email);

}
