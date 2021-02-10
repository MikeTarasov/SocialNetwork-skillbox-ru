package ru.skillbox.socialnetwork.dto.like.response;

import ru.skillbox.socialnetwork.dto.universal.BaseResponse;
import ru.skillbox.socialnetwork.dto.universal.ResponseFactory;
import ru.skillbox.socialnetwork.model.entity.Person;
import ru.skillbox.socialnetwork.model.entity.Post;
import ru.skillbox.socialnetwork.model.entity.PostLike;

import java.util.stream.Collectors;

public class LikeResponseFactory {
    public static BaseResponse getLikeDto(Post post) {
        return ResponseFactory.getBaseResponse(
                new LikeResponseDto(post.getLikes().size(),
                        post.getLikes().stream()
                                .map(PostLike::getPersonLike).map(Person::getId).collect(Collectors.toList()))
        );
    }

    public static BaseResponse isLiked(Boolean result) {
        return ResponseFactory.getBaseResponse(new IsLikeDto(result));
    }
}
