package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    @Query(value = "SELECT * FROM post WHERE" +
            "post.post_text ILIKE :text and " +
            "post.time between :date_from ::timestamp and :date_to ::timestamp " +
            "order by post.id desc", nativeQuery = true)
    List<Post> findPostsByTitleAndPeriod (
            @Param("text") String text,
            @Param("date_from") long timeFrom,
            @Param("date_to") long timeTo,
            Pageable pageable
            );

    @Query(value = "SELECT post.author_id FROM post WHERE post.id = :post_id", nativeQuery = true)
    long getAuthorId(@Param("post_id") long postId);

}