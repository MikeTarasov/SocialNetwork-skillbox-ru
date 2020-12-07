package ru.skillbox.socialnetwork.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.socialnetwork.model.entity.Post;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

}