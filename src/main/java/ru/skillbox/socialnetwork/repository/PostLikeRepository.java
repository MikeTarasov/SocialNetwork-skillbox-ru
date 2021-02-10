package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.PostLike;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("select pl from PostLike pl where pl.personLike.id = :personId and pl.postLike.id = :postId")
    Optional<PostLike> findPostLikeByPostIdAndPersonId(long postId, long personId);

    @Query("select pl from PostLike pl where pl.postLike.id = :postId")
    List<PostLike> findByPostId(long postId);

    @Query("select count (pl) from PostLike pl where pl.personLike.id = :personId and pl.postLike.id = :postId")
    int countPostLikesByPostIdAndPersonId(long postId, long personId);
}