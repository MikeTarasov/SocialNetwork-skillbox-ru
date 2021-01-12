package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Person;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    @Query(value = "select p from Person p where " +
            "(:firstName is null or p.firstName = :firstName) AND " +
            "(:lastName is null or p.lastName = :lastName) AND " +
            "(cast(:startDate as timestamp) is null or p.birthDate >= :startDate) AND " +
            "(cast(:endDate as timestamp) is null or p.birthDate <= :endDate)" +
            "order by p.lastName"
    )
    Page<Person> findPersons(String firstName, String lastName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}