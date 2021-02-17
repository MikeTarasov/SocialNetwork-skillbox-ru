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
import ru.skillbox.socialnetwork.api.responses.*;
import ru.skillbox.socialnetwork.model.entity.Notification;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostComment;
import ru.skillbox.socialnetwork.repository.*;
import ru.skillbox.socialnetwork.security.PersonDetailsService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PersonDetailsService personDetailsService;
    private final NotificationsRepository notificationsRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostCommentRepository commentRepository,
                       PersonDetailsService personDetailsService,
                       NotificationsRepository notificationsRepository,
                       NotificationTypeRepository notificationTypeRepository,
                       PostLikeRepository postLikeRepository) {

        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.postLikeRepository = postLikeRepository;
        this.personDetailsService = personDetailsService;
        this.notificationsRepository = notificationsRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    //@Override
    public Post findById(long id) {
        return postRepository.findPostById(Math.toIntExact(id));
    }

    public ResponseEntity<?> getApiPost(String text, Long dateFrom, Long dateTo, String authorName,
                                        Integer offset, Integer itemPerPage) {

        if (offset == null || itemPerPage == null) {
            offset = 0;
            itemPerPage = 20;
        }
        if (dateFrom == null) dateFrom = 0L;
        if (dateTo == null) dateTo = System.currentTimeMillis();
        text = convertNullString(text);
        authorName = convertNullString(authorName);

        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);

        List<Post> posts = postRepository.searchPostsByParametersNotBlockedAndNotDeleted(
                text, authorName, getMillisecondsToLocalDateTime(dateFrom), getMillisecondsToLocalDateTime(dateTo),
                personDetailsService.getCurrentUser().getId(), pageable);

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
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getPostEntityResponseByPost(optionalPost.get())));
    }

    public ResponseEntity<?> putApiPostId(long id, long publishDate, TitlePostTextRequest requestBody) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(400)
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
            return ResponseEntity.status(400)
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
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Post post = optionalPost.get();
        post.setIsDeleted(0);
        postRepository.saveAndFlush(post);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        getPostEntityResponseByPost(optionalPost.get())));
    }

    public ResponseEntity<?> getApiPostIdComments(long id, Integer offset, Integer itemPerPage) {

        StringBuilder errors = new StringBuilder();

        if (offset == null || itemPerPage == null) {
            offset = 0;
            itemPerPage = 20;
        }

        if (offset < 0) {
            errors.append("'offset' should be greater than 0. ");
        }
        if (itemPerPage <= 0) {
            errors.append("'itemPerPage' should be more than 0. ");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }
        Pageable pageable = PageRequest.of(offset / itemPerPage, itemPerPage);

        Optional<Post> optionalPost = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(400)
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
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        StringBuilder errors = new StringBuilder();

        if (requestBody.getCommentText().isEmpty()) {
            errors.append("'commentText' should not be empty");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
        }

        if (requestBody.getParentId() == null) {
            notificationsRepository.save(new Notification(
                    notificationTypeRepository.findById(2L).get(),
                    getMillisecondsToLocalDateTime(System.currentTimeMillis()),
                    postRepository.findById(id).get().getAuthor(),
                    id,
                    postRepository.findById(id).get().getAuthor().getEmail(),
                    0
            ));
        } else {
            notificationsRepository.save(new Notification(
                    notificationTypeRepository.findById(3L).get(),
                    getMillisecondsToLocalDateTime(System.currentTimeMillis()),
                    commentRepository.findById(requestBody.getParentId()).get().getPerson(),
                    requestBody.getParentId(),
                    commentRepository.findById(requestBody.getParentId()).get().getPerson().getEmail(),
                    0
            ));
        }

        PostComment comment = commentRepository.save(new PostComment(
                LocalDateTime.now(),
                requestBody.getParentId(),
                requestBody.getCommentText(),
                false,
                false,
                personDetailsService.getCurrentUser(),
                postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).get()
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
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        StringBuilder errors = new StringBuilder();

        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        if (requestBody.getCommentText().isEmpty()) {
            errors.append("'commentText' should not be empty");
        }
        if (!errors.toString().equals("")) {
            return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse(errors.toString().trim()));
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
        return recoverDeleteMessageComment(id, commentId, "delete");
    }

    public ResponseEntity<?> putApiPostIdCommentsCommentId(long id, long commentId) {
        return recoverDeleteMessageComment(id, commentId, "recover");
    }

    private ResponseEntity<?> recoverDeleteMessageComment(long id, long commentId, String type) {
        if (postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }
        Optional<PostComment> optionalPostComment = commentRepository.findById(commentId);
        if (optionalPostComment.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + " not found."));
        }
        PostComment comment = optionalPostComment.get();
        if (id != comment.getPost().getId()) {
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("PostComment with id = " + commentId + "is not found for post with id = " + id + "."));
        }
        switch (type.toLowerCase()) {
            case "recover":
                comment.setIsDeleted(false);
                commentRepository.saveAndFlush(comment);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                                getCommentEntityResponseByComment(comment)));
            case "delete":
                comment.setIsDeleted(true);
                commentRepository.saveAndFlush(comment);
                return ResponseEntity.status(200)
                        .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                                new IdResponse(commentId)));
            case "message":
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                                new MessageResponse()));
        }
        return ResponseEntity.status(400)
                .body(new ErrorErrorDescriptionResponse("Some error happened"));
    }

    public ResponseEntity<?> postApiPostIdReport(long id) {

        Optional<Post> optionalPost = postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now());

        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new ErrorErrorDescriptionResponse("Post with id = " + id + " not found."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse("", System.currentTimeMillis(),
                        new MessageResponse()));

    }

    public ResponseEntity<?> postApiPostIdCommentsCommentIdReport(long id, long commentId) {
        return recoverDeleteMessageComment(id, commentId, "message");
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
                        .from(post.getTime().atZone(ZoneId.systemDefault())
                                .toInstant()).getTime(),
                getPersonEntityResponseByPost(post),
                post.getTitle(),
                post.getPostText(),
                post.getIsBlocked() == 1,
                postLikeRepository
                        .countPostLikesByPostIdAndPersonId(post.getId(), personDetailsService.getCurrentUser().getId()),
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
                        .from(author.getRegDate().atZone(ZoneId.systemDefault())
                                .toInstant()).getTime(),
                author.getBirthDate() == null ? null : java.util.Date
                        .from(author.getBirthDate().atZone(ZoneId.systemDefault())
                                .toInstant()).getTime(),
                author.getEmail(),
                author.getPhone(),
                author.getPhoto(),
                author.getAbout(),
                author.getCity(),
                author.getCountry(),
                author.getMessagePermission(),
                author.getLastOnlineTime() == null ? null : java.util.Date
                        .from(author.getLastOnlineTime().atZone(ZoneId.systemDefault())
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
        return CommentEntityResponse
                .getCommentEntityResponseList(commentRepository.getCommentsByPostId(post.getId(), pageable),
                        commentRepository);
    }

    private CommentEntityResponse getCommentEntityResponseByComment(PostComment comment) {
        return new CommentEntityResponse(comment, commentRepository);
    }

    private LocalDateTime getMillisecondsToLocalDateTime(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    private String convertNullString(String s) {
        if (s == null) return "";
        return "%".concat(s).concat("%");
    }
}
