package ru.skillbox.socialnetwork.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.socialnetwork.api.requests.ParentIdCommentTextRequest;
import ru.skillbox.socialnetwork.api.requests.TitlePostTextRequest;
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
@Transactional
public class PostService {
    @Value("@{db.timezone}")
    private String timezone;

    private final int isDeleted = 0;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final AccountService accountService;

    @Autowired
    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository,
                       CommentRepository commentRepository, AccountService accountService) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.accountService = accountService;
    }

    public ResponseEntity<?> getApiPost(String text, long dateFrom, long dateTo,
                                        int offset, int itemPerPage) {
        StringBuilder errors = new StringBuilder();

        if (dateFrom > dateTo) {
            errors.append("'dateFrom' should be less or equal to 'dateTo'. ");
        }
        if (dateFrom > System.currentTimeMillis()) {
            errors.append("'dateFrom' should be less than current time. ");
        }
        if (dateFrom > dateTo) {
            errors.append("'dateFrom' should be less than or equal to 'dateTo'. ");
        }
        if (offset < 0) {
            errors.append("'offset' should be greater than 0. ");
        }
        if (itemPerPage <= 0) {
            errors.append("'itemPerPage' should be more than 0. ");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }

        Pageable pageable = PageRequest.of(offset, itemPerPage);
        List<Post> posts = postRepository
                .findByPostTextContainingIgnoreCaseAndTimeBetweenAndIsDeletedOrderByIdDesc(text,
                        getMillisecondsToLocalDateTime(dateFrom), getMillisecondsToLocalDateTime(dateTo),
                        isDeleted, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        posts.size(),
                        offset,
                        itemPerPage,
                        getPostEntityResponseListByPosts(posts)));
    }

    public ResponseEntity<?> getApiPostId(long id) {
        Optional<Post> optionalPost = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getPostEntityResponseByPost(optionalPost.get())));
    }

    public ResponseEntity<?> putApiPostId(long id, Long publishDate, TitlePostTextRequest requestBody) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Post post = optionalPost.get();
        post.setTitle(requestBody.getTitle());
        post.setPostText(requestBody.getPostText());
        post.setTime(getMillisecondsToLocalDateTime(publishDate == 0 ? System.currentTimeMillis() : publishDate));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getPostEntityResponseByPost(postRepository.saveAndFlush(post))));
    }

    public ResponseEntity<?> deleteApiPostId(long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Post post = optionalPost.get();
        post.setIsDeleted(1);
        postRepository.saveAndFlush(post);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(), new IdResponse(id)));
    }

    public ResponseEntity<?> putApiPostIdRecover(long id) {

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Post post = optionalPost.get();
        post.setIsDeleted(0);
        postRepository.saveAndFlush(post);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getPostEntityResponseByPost(optionalPost.get())));
    }

    public ResponseEntity<?> getApiPostIdComments(long id, int offset, int itemPerPage) {

        StringBuilder errors = new StringBuilder();

        if (offset < 0) {
            errors.append("'offset' should be greater than 0. ");
        }
        if (itemPerPage <= 0) {
            errors.append("'itemPerPage' should be more than 0. ");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }
        Pageable pageable = PageRequest.of(offset, itemPerPage);

        Optional<Post> optionalPost = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getCommentEntityResponseListByPost(optionalPost.get()).size(),
                        offset,
                        itemPerPage,
                        getCommentEntityResponseListByPost(optionalPost.get(), pageable)));
    }

    public ResponseEntity<?> postApiPostIdComments(long id, ParentIdCommentTextRequest requestBody) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        StringBuilder errors = new StringBuilder();

        if (requestBody.getCommentText().isEmpty()) {
            errors.append("'commentText' should not be empty");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }

        PostComment comment = commentRepository.save(new PostComment(
                getMillisecondsToLocalDateTime(System.currentTimeMillis()),
                requestBody.getParentId(),
                requestBody.getCommentText(),
                false,
                false,
                //accountService.getCurrentUser() - пока что не работает
                postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).get().getAuthor()
        ));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(
                        "",
                        System.currentTimeMillis(),
                        getCommentEntityResponseByComment(comment)
                ));
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(long id, long commentId,
                                                           ParentIdCommentTextRequest requestBody) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        StringBuilder errors = new StringBuilder();

        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        if (requestBody.getCommentText().isEmpty()) {
            errors.append("'commentText' should not be empty");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(200).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }

        PostComment comment = optionalPostComment.get();
        comment.setParentId(requestBody.getParentId());
        comment.setCommentText(requestBody.getCommentText());
        commentRepository.saveAndFlush(comment);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getCommentEntityResponseByComment(comment)));
    }


    public ResponseEntity<?> deleteApiPostIdCommentsCommentId(long id, long commentId) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        PostComment comment = optionalPostComment.get();
        comment.setIsBlocked(true);
        comment.setIsDeleted(true);
        commentRepository.saveAndFlush(comment);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(), new IdResponse(commentId)));
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(long id, long commentId) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        PostComment comment = optionalPostComment.get();
        comment.setIsBlocked(false);
        comment.setIsDeleted(false);
        commentRepository.saveAndFlush(comment);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getCommentEntityResponseByComment(comment)));
    }

    public ResponseEntity<?> postApiPostIdReport(long id) {

        Optional<Post> optionalPost = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());

        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        new MessageResponse()));

    }

    public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(long id, long commentId) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        new MessageResponse()));
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
                java.util.Date
                        .from(post.getTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                getPersonEntityResponseByPost(post),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked() == 1,
                1,
                getCommentEntityResponseListByPost(post)
        );
    }

    private PersonEntityResponse getPersonEntityResponseByPost(Post post) {
        Person author = post.getAuthor();
        return new PersonEntityResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                java.util.Date
                        .from(author.getRegDate().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                java.util.Date
                        .from(author.getBirthDate().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                author.getEmail(),
                author.getPhone(),
                author.getPhoto(),
                author.getAbout(),
                author.getCity(),
                author.getCountry(),
                author.getMessagePermission(),
                java.util.Date
                        .from(author.getLastOnlineTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                author.getIsBlocked() == 1
        );
    }


    private List<CommentEntityResponse> getCommentEntityResponseListByPost(Post post) {
        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        for (PostComment comment : commentRepository.getCommentsByPostId(post.getId())) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(comment));
        }
        return commentEntityResponseList;
    }

    private List<CommentEntityResponse> getCommentEntityResponseListByPost(Post post, Pageable pageable) {
        List<CommentEntityResponse> commentEntityResponseList = new ArrayList<>();
        List<PostComment> comments = commentRepository.getCommentsByPostId(post.getId(), pageable);
        for (PostComment comment : comments) {
            commentEntityResponseList.add(getCommentEntityResponseByComment(comment));
        }
        return commentEntityResponseList;
    }

    private CommentEntityResponse getCommentEntityResponseByComment(PostComment comment) {
        return new CommentEntityResponse(
                comment.getParentId(),
                comment.getCommentText(),
                comment.getId(),
                comment.getPost().getId(),
                java.util.Date
                        .from(comment.getTime().atZone(ZoneId.of("Europe/Moscow"))
                                .toInstant()).getTime(),
                comment.getPerson().getId(),
                comment.getIsBlocked()
        );
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        LocalDateTime localDateTime =
                Instant.ofEpochMilli(milliseconds).atZone(ZoneId.of("Europe/Moscow")).toLocalDateTime();
        return localDateTime;

    }

    //for testing
    public ResponseEntity<?> getPostBySearching(String text, long dateStart, long dateEnd, int isDeleted) {
        List<Post> posts = postRepository.findByPostTextContainingAndTimeBetweenAndIsDeletedOrderByIdDesc(text,
                getMillisecondsToLocalDateTime(dateStart), getMillisecondsToLocalDateTime(dateEnd),
                isDeleted);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
                        "",
                        System.currentTimeMillis(),
                        posts.size(),
                        0,
                        5,
                        getPostEntityResponseListByPosts(posts)));
    }
}
