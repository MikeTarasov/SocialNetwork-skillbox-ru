package ru.skillbox.socialnetwork.dto.like.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.skillbox.socialnetwork.dto.universal.Dto;
import java.util.List;
@Data
@AllArgsConstructor
public class LikeResponseDto  implements Dto {
    long likes;
    List<Long> users;
}
