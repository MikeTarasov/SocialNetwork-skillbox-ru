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
            "(:firstName = '' or lower(p.firstName) like lower(:firstName) or lower(p.lastName) like lower(:firstName)) AND " +
            "(:lastName = '' or lower(p.lastName) like lower(:lastName)) AND " +
            "(:city = '' or lower(p.city) like lower(:city)) AND " +
            "(:country = '' or lower(p.country) like lower(:country)) AND " +
            "(cast(:startDate as timestamp) is null or p.birthDate >= :startDate) AND " +
            "(cast(:endDate as timestamp) is null or p.birthDate <= :endDate) AND " +
            "p.isBlocked = 0 AND p.isDeleted = 0 and p.id <> :userId " +
            "order by p.lastName"
    )
    Page<Person> findPersons(String firstName, String lastName, String city, String country,
                             LocalDateTime startDate, LocalDateTime endDate, long userId, Pageable pageable);
}