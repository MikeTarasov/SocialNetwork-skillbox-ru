package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Friendship;
import ru.skillbox.socialnetwork.model.entity.Person;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Page<Friendship> findBySrcPersonAndCode(Person srcPerson, String code, Pageable paging);

    @Query(value = "select F from #{#entityName} F where F.srcPerson = :srcPerson and F.code = 'FRIEND' and upper(F.dstPerson.firstName) like concat('%',upper(:query),'%') ")
    Page<Friendship> findBySrcPersonAndCodeAndDstPersonName(Person srcPerson, String query, Pageable paging);
}
