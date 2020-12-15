package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Post;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPostTextLikeAndTimeAfterAndTimeBeforeAndIsDeletedFalseOrderByIdDesc(String text, long timeFrom,
                                                                 long timeTo, Pageable pageable);


    Optional<Post> findByIdAndTimeIsBefore(long id, long timeTo);

    @Query(value = "SELECT post.author_id FROM post WHERE post.id = :post_id", nativeQuery = true)
    long getAuthorId(@Param("post_id") long postId);

}