package ru.skillbox.socialnetwork.dto.like.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.skillbox.socialnetwork.dto.universal.Dto;

@Data
public class LikeRequest implements Dto {
    @JsonProperty("item_id")
    private long id;
    private String type;
}
