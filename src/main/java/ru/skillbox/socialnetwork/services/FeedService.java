package ru.skillbox.socialnetwork.services;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.responses.CommentEntityResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.repository.PostRepository;

@Service
public class FeedService {

  private final PostRepository postRepository;

  public FeedService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }


  public ResponseEntity<?> getFeed(String name, int offset, int itemPerPage) {

    Page<Post> allPosts = postRepository.findAll(getPageable(offset, itemPerPage));

    if (allPosts.isEmpty()) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new ErrorTimeTotalOffsetPerPageListDataResponse());
    }

    List<PostEntityResponse> listPostsEntityResponses = allPosts.stream()
        .map(this::getPostEntityResponse)
        .sorted(Comparator.comparing(PostEntityResponse::getTime).reversed())
        .collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.OK).body(
        getErrorTimeOffsetPerPageListDataResponse(listPostsEntityResponses, offset, itemPerPage));

  }

  private PostEntityResponse getPostEntityResponse(Post post) {
    return PostEntityResponse.builder()
        .id(post.getId())
        .time(post.getTimestamp())
        .author(PersonEntityResponse.getResponseEntity(post.getAuthor()))
        .title(post.getTitle())
        .postText(post.getPostText())
        .isBlocked(post.isBlocked())
        .likes(post.getPostLike().size())
        .comments(CommentEntityResponse.getCommentEntityResponseList(post.getComments()))
        .build();
  }

  private ErrorTimeTotalOffsetPerPageListDataResponse getErrorTimeOffsetPerPageListDataResponse(
      List<PostEntityResponse> listPostsEntityResponses, int offset, int itemPerPage) {
    return ErrorTimeTotalOffsetPerPageListDataResponse.builder()
        .error("")
        .timestamp(System.currentTimeMillis())
        .total(listPostsEntityResponses.size())
        .offset(offset)
        .perPage(itemPerPage)
        .data(listPostsEntityResponses)
        .build();
  }

  private Pageable getPageable(int offset, int itemPerPage) {
    return PageRequest.of(offset / itemPerPage, itemPerPage);
  }

}
