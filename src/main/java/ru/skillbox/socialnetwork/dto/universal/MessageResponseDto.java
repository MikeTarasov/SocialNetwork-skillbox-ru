package ru.skillbox.socialnetwork.dto.universal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponseDto implements Dto{
    private String message;
}
