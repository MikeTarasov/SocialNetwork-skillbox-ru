package ru.skillbox.socialnetwork.api.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListResponse {
    private Integer total;
    private Integer offset;
    private Integer perPage;
}
