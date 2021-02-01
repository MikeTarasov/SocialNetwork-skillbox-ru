package ru.skillbox.socialnetwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostLike;


@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query(value = "SELECT COUNT(*) FROM post_like WHERE post_like.post_id = :post_id", nativeQuery = true)
    int getAmountOfLikes (@Param("post_id") long postId);
    PostLike findPostLikeById(Integer id);

    PostLike findPostLikeByPostAndPerson(Post post, Person person);
}
