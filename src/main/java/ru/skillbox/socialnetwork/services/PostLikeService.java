package ru.skillbox.socialnetwork.services;

import ru.skillbox.socialnetwork.dto.like.request.LikeRequest;
import ru.skillbox.socialnetwork.dto.like.response.LikeResponseDto;
import ru.skillbox.socialnetwork.dto.universal.BaseResponse;
import ru.skillbox.socialnetwork.dto.universal.Dto;
import ru.skillbox.socialnetwork.dto.universal.ResponseFactory;
import ru.skillbox.socialnetwork.model.entity.*;
import ru.skillbox.socialnetwork.repository.CommentLikeRepository;
import ru.skillbox.socialnetwork.repository.CommentRepository;
import ru.skillbox.socialnetwork.repository.PostLikeRepository;
import ru.skillbox.socialnetwork.repository.PostRepository;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    //private final NotificationService notificationService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    public PostLikeService(PostLikeRepository postLikeRepository,CommentLikeRepository commentLikeRepository,PostRepository postRepository,PostService postService,CommentRepository commentRepository){
        this.postLikeRepository = postLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.postRepository = postRepository;
       // this.notificationService = notificationService;
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

    public boolean isUserHasLiked(Post post, Person person){//компилится

        return post.getLikes().stream().map(PostLike::getPersonPL).collect(Collectors.toList()).contains(person);
        //return true;
        //возвращает true если  поставлен  лайк или false если нет
        //нужны пост и человек написавший пост
        //нужны коммент и человек написавший коммент
    }


    public Dto deleteLike(Integer id, Integer postId, String type, Person person) {
        switch (type)
        {
            case "Post":
                Post post = postRepository.findPostById(id);
                PostLike postLike = postLikeRepository.findPostLikeByPostPLAndPersonPL(post, person);
                postLikeRepository.delete(postLike);
                return new LikeResponseDto(post.getLikes().size(), new ArrayList<>());
            case "Comment":
                PostComment comment = commentRepository.findPostCommentPCById(id);
                CommentLike commentLike = commentLikeRepository.findCommentLikeByCommentCLAndPersonCL(comment, person);
                commentLikeRepository.delete(commentLike);
                return new LikeResponseDto(comment.getCommentLikes().size(), new ArrayList<>());
        }
        return null;
        //удаляет лайк с поста или коммента если нажать еще раз
        //нужны пост или коммент(их id) и человек которому принадлежат пост или коммент
    }

    public PostLike getListOfLikes(Integer id, String type) {//компилится
       // return postLikeRepository.getAmountOfLikes(id);
        return postLikeRepository.findPostLikeById(id);
        //возвращает  значение(id) метода(найтипостЛайк по id) в репозитории лайков
        //возвращает  значение(id) метода(найтиКомментЛайк по id) в репозитории лайков
    }


    public Dto  save(PostLike like) {
        if (!isUserHasLiked(like.getPostPL(), like.getPersonPL()))
        {
            postLikeRepository.save(like);
        }

        return new LikeResponseDto(like.getPostPL().getLikes().size(), like.getPostPL().getLikes().stream()
                .map(PostLike::getPersonPL).map(Person::getId).collect(Collectors.toList()));
        //сохраняет лайк к посту или комменту
        //уведомление о поставке лайка
    }


    public BaseResponse puLike(LikeRequest request, Person person,Optional<Long> publishDate){
        switch (request.getType())
        {
            case "Post":
                Post post = postService.findById(request.getId());
                postLikeRepository.save(
                        PostLike.builder()
                                .personPL(person)
                                .postPL(post)
                                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(publishDate.orElseGet(System::currentTimeMillis)),
                                        TimeZone.getDefault().toZoneId()))
                                .build());
                return ResponseFactory.responseOk();
            case "Comment":
                Optional<PostComment> comment = commentRepository.findById(request.getId());
                commentLikeRepository.save(
                        CommentLike.builder()
                                .personCL(person)
                                .commentCL(comment.get())
                                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(publishDate.orElseGet(System::currentTimeMillis)),
                                        TimeZone.getDefault().toZoneId()))
                                .build());
                 return ResponseFactory.responseOk();
        }
        return null;
        //уведомляет о получении лайка к посту ил комменту
    }

}
