package ru.skillbox.socialnetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.services.PostService;

import java.util.Optional;

@RestController
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/")
  public ResponseEntity<?> getApiPost(@Param("text") String text, @Param("date_from") long dateFrom,
                                      @Param("date_to") long dateTo, @Param("offset") int offset,
                                      @Param("itemPerPage") int itemPerPage) {
    return postService.getApiPost(text, dateFrom, dateTo, offset, itemPerPage);
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getApiPostId(@PathVariable("id") long id) {
    return postService.getApiPostId(id);
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> putApiPostId(@PathVariable("id") long id,
                                        @Param("publish_date") Optional<Long> publishDate,
                                        @RequestBody TitlePostTextRequest requestBody) {
    return postService.putApiPostId(id, publishDate, requestBody);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteApiPostId(@PathVariable("id") long id) {
    return postService.deleteApiPostId(id);
  }


  @PutMapping("/{id}/recover")
  public ResponseEntity<?> putApiPostIdRecover(@PathVariable("id") long id) {
    return postService.putApiPostIdRecover(id);
  }


  @GetMapping("/{id}/comments")
  public ResponseEntity<?> getApiPostIdComments(@PathVariable("id") long id, @Param("offset") int offset,
                                                @Param("itemPerPage") int itemPerPage) {
    return postService.getApiPostIdComments(id, offset, itemPerPage);
  }


  @PostMapping("/{id}/comments")
  public ResponseEntity<?> postApiPostIdComments(@PathVariable("id") long id,
                                                 @RequestBody ParentIdCommentTextRequest requestBody) {
    return postService.postApiPostIdComments(id, requestBody);
  }


  @PutMapping("/{id}/comments/{comment_id}")
  public ResponseEntity<?> putApiPostIdCommentsCommentId(@PathVariable("id") long id,
                                                         @PathVariable("comment_id") long commentId,
                                                         @RequestBody ParentIdCommentTextRequest requestBody) {
    return postService.putApiPostIdCommentsCommentId(id, commentId, requestBody);
  }


  @DeleteMapping("/{id}/comments/{comment_id}")
  public ResponseEntity<?> deleteApiPostIdCommentsCommentId(@PathVariable("id") long id,
                                                            @PathVariable("comment_id") long commentId) {
    return postService.deleteApiPostIdCommentsCommentId(id, commentId);
  }


  @PutMapping("/{id}/comments/{comment_id}/recover")
  public ResponseEntity<?> putApiPostIdCommentsCommentId(@PathVariable("id") long id,
                                                         @PathVariable("comment_id") long commentId) {
    return postService.putApiPostIdCommentsCommentId(id, commentId);
  }


  @PostMapping("/{id}/report")
  public ResponseEntity<?> postApiPostIdReport(@PathVariable("id") long id) {
    return postService.postApiPostIdReport(id);
  }


  @PostMapping("/{id}/comments/{comment_id}/report")
  public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(@PathVariable("id") long id,
                                                                @PathVariable("comment_id") long commentId) {
    return postService.postApiPostIdCommentsCommentIdReport(id, commentId);
  }
}