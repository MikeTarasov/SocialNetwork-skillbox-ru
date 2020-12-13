package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PersonRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostService {

    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, PersonRepository personRepository,
                       PostLikeRepository postLikeRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.personRepository = personRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
    }


    public ResponseEntity<?> getApiPost(
            @Param("text") String text,
            @Param("date_from") long dateFrom,
            @Param("date_to") long dateTo,
            @Param("offset") int offset,
            @Param("itemPerPage") int itemPerPage) {

        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<Post> posts = postRepository.findPostsByTitleAndPeriod(text, dateFrom, dateTo, pageable);

        //работа с ответом
        ErrorTimeTotalOffsetPerPageListDataResponse response = new ErrorTimeTotalOffsetPerPageListDataResponse();

        response.setError("OK");
        response.setTimestamp(System.currentTimeMillis());
        response.setTotal(posts.size());
        response.setOffset(offset);
        response.setPerPage(itemPerPage);


        List<PostEntityResponse> postEntityResponseList = new ArrayList<>();
        for (Post post : posts) {
            PostEntityResponse postEntityResponse = new PostEntityResponse();
            PersonEntityResponse personEntityResponse = new PersonEntityResponse();
            IdTitleResponse city = new IdTitleResponse();
            IdTitleResponse country = new IdTitleResponse();

            postEntityResponse.setId(post.getId());
            postEntityResponse.setTime(post.getTime());

            long authorId = postRepository.getAuthorId(post.getId()); //получаем автора поста
            Person author = personRepository.findById(authorId).get();

            personEntityResponse.setId(author.getId());
            personEntityResponse.setFirstName(author.getFirstName());
            personEntityResponse.setLastName(author.getLastName());
            personEntityResponse.setRegDate(author.getRegDate());
            personEntityResponse.setBirthDate(author.getBirthDate());
            personEntityResponse.setEmail(author.getEmail());
            personEntityResponse.setPhone(author.getPhone());
            personEntityResponse.setPhoto(author.getPhoto());
            personEntityResponse.setAbout(author.getAbout());

            city.setTitle(author.getCity());
            country.setTitle(author.getCountry());

            personEntityResponse.setCity(city);
            personEntityResponse.setCountry(country);
            personEntityResponse.setMessagesPermission(author.getMessagePermission());
            personEntityResponse.setLastOnlineTime(author.getLastOnlineTime());
            if (author.getIsBlocked() == 1) {
                personEntityResponse.setBlocked(true);
            } else {
                personEntityResponse.setBlocked(false);
            }

            postEntityResponse.setTitle(post.getTitle());
            postEntityResponse.setPostText(post.getPostText());
            if (post.getIsBlocked() == 1) {
                postEntityResponse.setBlocked(true);
            } else {
                postEntityResponse.setBlocked(false);
            }

            postEntityResponse.setLikes(postLikeRepository.getAmountOfLikes(post.getId()));

            List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();

            List<PostComment> comments = commentRepository.getCommentsByPostId(post.getId());
            for (PostComment comment : comments) {
                CommentEntityResponse commentEntityResponse = new CommentEntityResponse();
                commentEntityResponse.setParentId(comment.getParentId());
                commentEntityResponse.setCommentText(comment.getCommentText());
                commentEntityResponse.setId(comment.getId());
                commentEntityResponse.setPostId(post.getId());
                commentEntityResponse.setTime(comment.getTime());
                commentEntityResponse.setAuthorId(comment.getAuthorId());
                if (comment.getIsBlocked() == 1) {
                    commentEntityResponse.setBlocked(true);
                } else {
                    commentEntityResponse.setBlocked(false);
                }
                commentEntityResponseList.add(commentEntityResponse);

            }

            postEntityResponse.setComments(commentEntityResponseList);

        }

        response.setData(postEntityResponseList);

        return ResponseEntity.status(200)
                .body(response);
    }


    public ResponseEntity<?> getApiPostId(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse());
    }

    public ResponseEntity<?> putApiPostId(
            @PathVariable("id") int id,
            @Param("publish_date") long publishDate,
            @RequestBody TitlePostTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse());
    }

    public ResponseEntity<?> deleteApiPostId(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 123456789, new IdResponse()));
    }

    public ResponseEntity<?> putApiPostIdRecover(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 123456789, new PostEntityResponse()));
    }

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

    public ResponseEntity<?> postApiPostIdComments(@PathVariable("id") int id,
                                                   @RequestBody ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 1235214, new CommentEntityResponse()));
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(
            @PathVariable("id") int id,
            @PathVariable("comment_id") int commentId,
            @RequestBody ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 123456, new CommentEntityResponse()));
    }


    public ResponseEntity<?> deleteApiPostIdCommentsCommentId(@PathVariable("id") int id,
                                                              @PathVariable("comment_id") int commentId) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 123456, new IdResponse(123)));
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(@PathVariable("id") int id,
                                                           @PathVariable("comment_id") int commentId) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeListDataResponse("", 12345, new ArrayList<CommentEntityResponse>()));
    }

    public ResponseEntity<?> postApiPostIdReport(@PathVariable("id") int id) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 123, new MessageResponse("")));
    }

    public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(@PathVariable("id") int id,
                                                                  @PathVariable("comment_id") int commentId) {
        return ResponseEntity.status(200)
                .body(new ErrorTimeDataResponse("", 1234, new MessageResponse("")));
    }

}
