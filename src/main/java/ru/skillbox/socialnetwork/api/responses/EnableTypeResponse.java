package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnableTypeResponse {

    private boolean enable;
    private String type;
}