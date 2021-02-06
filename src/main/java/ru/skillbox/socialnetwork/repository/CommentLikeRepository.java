package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.socialnetwork.model.entity.CommentLike;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.PostComment;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
//    CommentLike findCommentLikeById(long id);
    CommentLike findCommentLikeByCommentCLAndPersonCL(PostComment comment, Person person);
}
