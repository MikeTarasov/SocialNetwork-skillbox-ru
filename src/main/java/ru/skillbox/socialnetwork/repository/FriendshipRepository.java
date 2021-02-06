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

    Page<Friendship> findByDstPersonAndCode(Person dstPerson, String code, Pageable paging);

    @Query(value = "select F from #{#entityName} F where F.dstPerson = :dstPerson and F.code = :code and upper(F.srcPerson.firstName) like concat('%',upper(:name),'%') ")
    Page<Friendship> findByDstPersonAndDstPersonNameAndCode(Person dstPerson, String name, String code, Pageable paging);
}
