package ru.skillbox.socialnetwork.dto.like.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.socialnetwork.dto.universal.Dto;
@Data
@AllArgsConstructor
public class IsLikeDto implements Dto {
    private Boolean likes;
}
