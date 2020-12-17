package ru.skillbox.socialnetwork.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Long> {

    List<Post> findByTimeBefore(LocalDateTime time);

}