package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.CommentEntityResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorErrorDescriptionResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeTotalOffsetPerPageListDataResponse;
import ru.skillbox.socialnetwork.api.responses.IdTitleResponse;
import ru.skillbox.socialnetwork.api.responses.PersonEntityResponse;
import ru.skillbox.socialnetwork.api.responses.PostEntityResponse;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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

    public ResponseEntity<?> getApiPost(String text, long dateFrom, long dateTo,
                                        int offset, int itemPerPage) {

        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<Post> posts = postRepository.findPostsByTitleAndPeriod(text, dateFrom, dateTo, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        posts.size(),
                        offset,
                        itemPerPage,
                        getPostEntityResponseListByPosts(posts))
                );
    }

    public ResponseEntity<?> getApiPostId(long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("User not found."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getPostEntityResponseByPost(optionalPost.get()))
                );
    }

    @Transactional
    public ResponseEntity<?> putApiPostId(long id, long publishDate, TitlePostTextRequest requestBody) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("User not found."));
        }
        Post post = optionalPost.get();
        post.setTitle(requestBody.getTitle());
        post.setPostText(requestBody.getPostText());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getPostEntityResponseByPost(postRepository.saveAndFlush(post)))
                );

    }

    public ResponseEntity<?> deleteApiPostId(long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdRecover(long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> getApiPostIdComments(long id, int offset, int itemPerPage) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdComments(long id, ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(long id, int commentId,
                                                           ParentIdCommentTextRequest requestBody) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }


    public ResponseEntity<?> deleteApiPostIdCommentsCommentId(long id, int commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(long id, int commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdReport(long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(long id, int commentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(id);
    }

    private List<PostEntityResponse> getPostEntityResponseListByPosts(List<Post> posts) {
        List<PostEntityResponse> postEntityResponseList = new ArrayList<>();
        for (Post post : posts) {
            postEntityResponseList.add(getPostEntityResponseByPost(post));
        }
        return postEntityResponseList;
    }

    private PostEntityResponse getPostEntityResponseByPost(Post post) {
        return new PostEntityResponse(
                post.getId(),
                post.getTime(),
                getPersonEntityResponseByPost(post),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked() == 1,
                postLikeRepository.getAmountOfLikes(post.getId()),
                getCommentsByPost(post),
                null);
    }

    private PersonEntityResponse getPersonEntityResponseByPost(Post post) {
        Person author = post.getAuthor();
        return new PersonEntityResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getRegDate(),
                author.getBirthDate(),
                author.getEmail(),
                author.getPhone(),
                author.getPhoto(),
                author.getAbout(),
                getIdTitleResponse(author.getCity()),
                getIdTitleResponse(author.getCountry()),
                author.getMessagePermission(),
                author.getLastOnlineTime(),
                author.getIsBlocked() == 1
        );
    }

    private IdTitleResponse getIdTitleResponse(String title) {
        IdTitleResponse country = new IdTitleResponse();
        country.setTitle(title);
        return country;
    }

    private List<CommentEntityResponse> getCommentsByPost(Post post) {

        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        List<PostComment> comments = commentRepository.getCommentsByPostId(post.getId());
        for (PostComment comment : comments) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(post, comment));
        }
        return commentEntityResponseList;
    }

    private CommentEntityResponse getCommentEntityResponseByComment(Post post, PostComment comment) {
        return new CommentEntityResponse(
                comment.getId(),
                comment.getParentId(),
                post.getId(),
                comment.getTime(),
                comment.getAuthorId(),
                comment.getCommentText(),
                comment.getIsBlocked() == 1
        );
    }
}
