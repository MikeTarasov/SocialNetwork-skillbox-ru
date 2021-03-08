package ru.skillbox.socialnetwork.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entities.PostComment;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    PostComment findPostCommentById(long id);

    @Query(value = "SELECT * FROM post_comment WHERE post_comment.post_id = :post_id", nativeQuery = true)
    List<PostComment> getCommentsByPostId(@Param("post_id") long postId);

    @Query(value = "SELECT * FROM post_comment WHERE post_comment.post_id = :post_id", nativeQuery = true)
    List<PostComment> getCommentsByPostId(@Param("post_id") long postId, Pageable pageable);

    List<PostComment> findByParentId(Long parentId);
}
