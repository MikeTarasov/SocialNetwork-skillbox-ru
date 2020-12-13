package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
    }

    private List<CommentEntityResponse> getCommentsByPost (Post post) {

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
            commentEntityResponse.setBlocked(comment.getIsBlocked() == 1);

            commentEntityResponseList.add(commentEntityResponse);
        }
        return commentEntityResponseList;
    }

    private PersonEntityResponse getPersonEntityResponseByPost (Post post) {
        Person author = post.getAuthor();
        PersonEntityResponse personEntityResponse = new PersonEntityResponse();
        IdTitleResponse city = new IdTitleResponse();
        IdTitleResponse country = new IdTitleResponse();

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
        personEntityResponse.setBlocked(author.getIsBlocked() == 1);

        return personEntityResponse;
    }

    public ResponseEntity<?> getApiPost(
            String text,
            long dateFrom,
            long dateTo,
            int offset,
            int itemPerPage) {

        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<Post> posts = postRepository.findPostsByTitleAndPeriod(text, dateFrom, dateTo, pageable);

        //работа с ответом
        ErrorTimeTotalOffsetPerPageListDataResponse response = new ErrorTimeTotalOffsetPerPageListDataResponse();

        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        response.setTotal(posts.size());
        response.setOffset(offset);
        response.setPerPage(itemPerPage);

        List<PostEntityResponse> postEntityResponseList = new ArrayList<>();

        for (Post post : posts) {

            PostEntityResponse postEntityResponse = new PostEntityResponse();

            postEntityResponse.setId(post.getId());
            postEntityResponse.setTime(post.getTime());
            postEntityResponse.setAuthor(getPersonEntityResponseByPost(post));
            postEntityResponse.setTitle(post.getTitle());
            postEntityResponse.setPostText(post.getPostText());
            postEntityResponse.setBlocked(post.getIsBlocked() == 1);
            postEntityResponse.setLikes(postLikeRepository.getAmountOfLikes(post.getId()));
            postEntityResponse.setComments(getCommentsByPost(post));

            postEntityResponseList.add(postEntityResponse);

        }

        response.setData(postEntityResponseList);

        return ResponseEntity.status(200)
                .body(response);
    }


    public ResponseEntity<?> getApiPostId(int id) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> putApiPostId(
            int id,
            long publishDate,
            TitlePostTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> deleteApiPostId(int id) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdRecover(int id) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> getApiPostIdComments(int id,
                                                  int offset, int itemPerPage) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdComments(int id,
                                                   ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(
            int id,
            int commentId,
            ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(200)
                .body(id);
    }


    public ResponseEntity<?> deleteApiPostIdCommentsCommentId(int id,
                                                              int commentId) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(int id,
                                                           int commentId) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdReport(int id) {
        return ResponseEntity.status(200)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(int id,
                                                                  int commentId) {
        return ResponseEntity.status(200)
                .body(id);
    }

}
