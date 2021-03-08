package ru.skillbox.socialnetwork.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.socialnetwork.api.requests.ItemIdTypeRequest;
import ru.skillbox.socialnetwork.api.responses.ErrorErrorDescriptionResponse;
import ru.skillbox.socialnetwork.api.responses.ErrorTimeDataResponse;
import ru.skillbox.socialnetwork.api.responses.LikesCountResponse;
import ru.skillbox.socialnetwork.api.responses.LikesListUsersResponse;
import ru.skillbox.socialnetwork.config.security.PersonDetailsService;
import ru.skillbox.socialnetwork.model.entities.CommentLike;
import ru.skillbox.socialnetwork.model.entities.Person;
import ru.skillbox.socialnetwork.model.entities.PostComment;
import ru.skillbox.socialnetwork.model.entities.PostLike;
import ru.skillbox.socialnetwork.repositories.CommentLikeRepository;
import ru.skillbox.socialnetwork.repositories.PostCommentRepository;
import ru.skillbox.socialnetwork.repositories.PostLikeRepository;
import ru.skillbox.socialnetwork.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final PersonDetailsService personDetailsService;
    private final PostCommentRepository commentRepository;

    public PostLikeService(PostLikeRepository postLikeRepository,
                           CommentLikeRepository commentLikeRepository,
                           PostRepository postRepository,
                           PersonDetailsService personDetailsService,
                           PostCommentRepository commentRepository) {
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postRepository = postRepository;
        this.personDetailsService = personDetailsService;
        this.commentRepository = commentRepository;
    }

    public boolean isUserHasLiked(Long userId, long itemId, String type) {//компилится
        Person person = personDetailsService.getCurrentUser();
        userId = userId == null ? person.getId() : userId;

        switch (type.toLowerCase()) {
            case "post":
                return postLikeRepository.findPostLikeByPostIdAndPersonId(itemId, userId).isPresent();
            case "comment":
                return commentLikeRepository.findByCommentIdAndPersonId(itemId, userId).isPresent();
        }
        return false;
        //return true;
        //возвращает true если  поставлен  лайк или false если нет
        //нужны пост и человек написавший пост
        //нужны коммент и человек написавший коммент
    }

    public LikesListUsersResponse getListOfLikes(long itemId, String type) {
        List<Long> userIdList = new ArrayList<>();
        long count = 0L;

        switch (type.toLowerCase()) {
            case "post":
                List<PostLike> postLikes = postLikeRepository.findByPostId(itemId);
                count = postLikes.size();
                postLikes.forEach(postLike -> userIdList.add(postLike.getPersonLike().getId()));
                break;
            case "comment":
                List<CommentLike> commentLikes = commentLikeRepository.findByCommentId(itemId);
                count = commentLikes.size();
                commentLikes.forEach(commentLike -> userIdList.add(commentLike.getPersonCL().getId()));
        }

        return new LikesListUsersResponse(count, userIdList);
    }


    public ResponseEntity<?> putLike(ItemIdTypeRequest request) {
        Person person = personDetailsService.getCurrentUser();

        if (!isUserHasLiked(person.getId(), request.getId(), request.getType())) {
            switch (request.getType().toLowerCase()) {
                case "post":
                    postLikeRepository
                            .saveAndFlush(new PostLike(person, postRepository.findPostById(request.getId())));
                    break;
                case "comment":
                    commentLikeRepository
                            .saveAndFlush(new CommentLike(person, commentRepository.findPostCommentById(request.getId())));
                    break;
            }
            return ResponseEntity.status(200)
                    .body(new ErrorTimeDataResponse(getListOfLikes(request.getId(), request.getType())));
        }
        return ResponseEntity.status(400).body(new ErrorErrorDescriptionResponse("Already liked"));
        //уведомляет о получении лайка к посту ил комменту
    }


    public ResponseEntity<?> deleteLike(Long id, String type) {
        Person person = personDetailsService.getCurrentUser();
        int count = 0;
        switch (type.toLowerCase()) {
            case "post":
                PostLike postLike = postLikeRepository.findPostLikeByPostIdAndPersonId(id, person.getId()).orElseThrow();
                postLikeRepository.delete(postLike);
                count = postLikeRepository.countPostLikesByPostIdAndPersonId(person.getId(), id);
                break;
            case "comment":
                PostComment comment = commentRepository.findPostCommentById(id);
                CommentLike commentLike = commentLikeRepository.findCommentLikeByCommentCLAndPersonCL(comment, person);
                commentLikeRepository.delete(commentLike);
                count = commentLikeRepository.countCommentLikesByCommentIdAndPersonId(id, person.getId());
                break;
        }
        return ResponseEntity.status(200).body(new ErrorTimeDataResponse(new LikesCountResponse(count)));
        //удаляет лайк с поста или коммента если нажать еще раз
        //нужны пост или коммент(их id) и человек которому принадлежат пост или коммент
    }
}
