package ru.skillbox.socialnetwork.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entities.Person;
import ru.skillbox.socialnetwork.model.entities.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndTimeIsBefore(long id, LocalDateTime time);

    Page<Post> findByAuthorAndTimeBeforeAndIsBlockedAndIsDeleted(
            Person person, LocalDateTime timeBefore, int isBlocked, int isDeleted, Pageable paging);

    List<Post> findByPostTextContainingAndTimeBetweenAndIsDeletedOrderByIdDesc(
            String postText, LocalDateTime dateStart, LocalDateTime dateEnd, int isDeleted);

    @Query("select post from Post post join Person pers on post.author.id = pers.id where " +
            "pers.isBlocked = 0 and pers.isDeleted = 0 and " +
            "(lower(post.title) like lower(:text) or lower(post.postText) like lower(:text)) and " +
            "(:author = '' or lower(post.author.firstName) like lower(:author) or " +
            "lower(post.author.lastName) like lower(:author)) and " +
            "post.time >= :from and post.time <= :to and post.isBlocked = 0 and post.isDeleted = 0 and " +
            "pers.id <> :userId order by post.time desc")
    List<Post> searchPostsByParametersNotBlockedAndNotDeleted(
            String text, String author, LocalDateTime from, LocalDateTime to, long userId, Pageable pageable);

    Post findPostById(long id);
}