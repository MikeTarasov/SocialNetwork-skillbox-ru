package ru.skillbox.socialnetwork.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.PostComment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<PostComment, Long> {

    @Query(value = "SELECT * FROM post_comment WHERE post_comment.post_id = :post_id", nativeQuery = true)
    List<PostComment> getCommentsByPostId (@Param("post_id") long postId);

    @Query(value = "SELECT * FROM post_comment WHERE post_comment.post_id = :post_id", nativeQuery = true)
    List<PostComment> getCommentsByPostId (@Param("post_id") long postId, Pageable pageable);

    Optional<PostComment> findByCommentText(String commentText);
}
