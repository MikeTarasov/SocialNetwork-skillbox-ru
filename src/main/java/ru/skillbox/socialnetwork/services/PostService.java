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
import ru.skillbox.socialnetwork.config.security.PersonDetailsService;
import ru.skillbox.socialnetwork.model.entities.Notification;
import ru.skillbox.socialnetwork.model.entities.Post;
import ru.skillbox.socialnetwork.model.entities.PostComment;
import ru.skillbox.socialnetwork.repositories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.skillbox.socialnetwork.services.ConvertTimeService.getLocalDateTime;

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
                text, authorName, getLocalDateTime(dateFrom), getLocalDateTime(dateTo),
                personDetailsService.getCurrentUser().getId(), pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeTotalOffsetPerPageListDataResponse(
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
                .body(new ErrorTimeDataResponse(getPostEntityResponseByPost(optionalPost.get())));
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
        post.setTime(getLocalDateTime(publishDate == 0 ? System.currentTimeMillis() : publishDate));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(getPostEntityResponseByPost(postRepository.saveAndFlush(post))));
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
                .body(new ErrorTimeDataResponse(new IdResponse(id)));
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
                .body(new ErrorTimeDataResponse(getPostEntityResponseByPost(optionalPost.get())));
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

        PostComment comment = commentRepository.save(new PostComment(
                LocalDateTime.now(),
                requestBody.getParentId(),
                requestBody.getCommentText(),
                false,
                false,
                personDetailsService.getCurrentUser(),
                postRepository.findByIdAndTimeIsBefore(id, LocalDateTime.now()).get()
        ));

        if (requestBody.getParentId() == null) {
            notificationsRepository.save(new Notification(
                    notificationTypeRepository.findById(2L).get(),
                    LocalDateTime.now(),
                    postRepository.findById(id).get().getAuthor(),
                    comment.getId(),
                    postRepository.findById(id).get().getAuthor().getEmail(),
                    0
            ));
        } else {
            notificationsRepository.save(new Notification(
                    notificationTypeRepository.findById(3L).get(),
                    LocalDateTime.now(),
                    commentRepository.findById(requestBody.getParentId()).get().getPerson(),
                    comment.getId(),
                    commentRepository.findById(requestBody.getParentId()).get().getPerson().getEmail(),
                    0
            ));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ErrorTimeDataResponse(getCommentEntityResponseByComment(comment)));
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
                .body(new ErrorTimeDataResponse(getCommentEntityResponseByComment(comment)));
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
                        .body(new ErrorTimeDataResponse(getCommentEntityResponseByComment(comment)));
            case "delete":
                comment.setIsDeleted(true);
                commentRepository.saveAndFlush(comment);
                return ResponseEntity.status(200)
                        .body(new ErrorTimeDataResponse(new IdResponse(commentId)));
            case "message":
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ErrorTimeDataResponse(new MessageResponse()));
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
                .body(new ErrorTimeDataResponse(new MessageResponse()));

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
        return new PostEntityResponse(post, commentRepository);
    }

    private PersonEntityResponse getPersonEntityResponseByPost(Post post) {
        return new PersonEntityResponse(post.getAuthor());
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

    private String convertNullString(String s) {
        if (s == null) return "";
        return "%".concat(s).concat("%");
    }
}