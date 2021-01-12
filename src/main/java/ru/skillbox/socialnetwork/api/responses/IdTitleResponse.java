package ru.skillbox.socialnetwork.api.responses;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class IdTitleResponse {

    private int id;
    private String title;

    public IdTitleResponse(String title) {
        if (title != null) {
            this.id = title.hashCode();
            this.title = title;
        } else {
            id = 0;
            this.title = "";
        }
    }
}