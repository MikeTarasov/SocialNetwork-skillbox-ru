package ru.skillbox.socialnetwork.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.socialnetwork.model.entities.CommentLike;
import ru.skillbox.socialnetwork.model.entities.Person;
import ru.skillbox.socialnetwork.model.entities.PostComment;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    CommentLike findCommentLikeByCommentCLAndPersonCL(PostComment comment, Person person);

    @Query("select cl from CommentLike cl where cl.commentCL.id = :commentId and cl.personCL.id = :personId")
    Optional<CommentLike> findByCommentIdAndPersonId(long commentId, long personId);

    @Query("select cl from CommentLike cl where cl.commentCL.id = :commentId")
    List<CommentLike> findByCommentId(long commentId);

    @Query("select count (cl) from CommentLike cl where cl.commentCL.id = :commentId and cl.personCL.id = :personId")
    int countCommentLikesByCommentIdAndPersonId(long commentId, long personId);
}
