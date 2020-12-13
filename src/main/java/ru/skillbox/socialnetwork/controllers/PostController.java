package ru.skillbox.socialnetwork.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.CommentEntityResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeListDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.IdResponse;
import ru.skillbox.socialnetwork.api.responses.MessageResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.services.PostService;

@RestController
@RequestMapping("/post")
public class PostController {

  private final PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/")
  public ResponseEntity<?> getApiPost(
      @Param("text") String text,
      @Param("date_from") long dateFrom,
      @Param("date_to") long dateTo,
      @Param("offset") int offset,
      @Param("itemPerPage") int itemPerPage) {

    return postService.getApiPost(text, dateFrom, dateTo, offset, itemPerPage);
  }


  @GetMapping("/{id}")
  public ResponseEntity<?> getApiPostId(@PathVariable("id") int id) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456, new PostEntityResponse()));
  }


  @PutMapping("/{id}")
  public ResponseEntity<?> putApiPostId(
      @PathVariable("id") int id,
      @Param("publish_date") long publishDate,
      @RequestBody TitlePostTextRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456789, new PostEntityResponse()));
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteApiPostId(@PathVariable("id") int id) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456789, new IdResponse()));
  }


  @PutMapping("/{id}/recover")
  public ResponseEntity<?> putApiPostIdRecover(@PathVariable("id") int id) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456789, new PostEntityResponse()));
  }


  @GetMapping("/{id}/comments")
  public ResponseEntity<?> getApiPostIdComments(@PathVariable("id") int id,
      @Param("offset") int offset, @Param("itemPerPage") int itemPerPage) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
            "error",
            12346589,
            125,
            0,
            20,
            new ArrayList<CommentEntityResponse>()
        ));
  }


  @PostMapping("/{id}/comments")
  public ResponseEntity<?> postApiPostIdComments(@PathVariable("id") int id,
      @RequestBody ParentIdCommentTextRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1235214, new CommentEntityResponse()));
  }


  @PutMapping("/{id}/comments/{comment_id}")
  public ResponseEntity<?> putApiPostIdCommentsCommentId(
      @PathVariable("id") int id,
      @PathVariable("comment_id") int commentId,
      @RequestBody ParentIdCommentTextRequest requestBody) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456, new CommentEntityResponse()));
  }


  @DeleteMapping("/{id}/comments/{comment_id}")
  public ResponseEntity<?> deleteApiPostIdCommentsCommentId(@PathVariable("id") int id,
      @PathVariable("comment_id") int commentId) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123456, new IdResponse(123)));
  }


  @PutMapping("/{id}/comments/{comment_id}/recover")
  public ResponseEntity<?> putApiPostIdCommentsCommentId(@PathVariable("id") int id,
      @PathVariable("comment_id") int commentId) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeListDataResponse("", 12345, new ArrayList<CommentEntityResponse>()));
  }


  @PostMapping("/{id}/report")
  public ResponseEntity<?> postApiPostIdReport(@PathVariable("id") int id) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 123, new MessageResponse("")));
  }


  @PostMapping("/{id}/comments/{comment_id}/report")
  public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(@PathVariable("id") int id,
      @PathVariable("comment_id") int commentId) {
    return ResponseEntity.status(200)
        .body(new ErrorTimeDataResponse("", 1234, new MessageResponse("")));
  }
}