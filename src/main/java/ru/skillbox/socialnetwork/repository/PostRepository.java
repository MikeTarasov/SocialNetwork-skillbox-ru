package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    Page<Post> findByAuthorAndTimeBeforeAndIsBlockedAndIsDeleted(Person person, LocalDateTime timeBefore, int isBlocked, int isDeleted, Pageable paging);
}