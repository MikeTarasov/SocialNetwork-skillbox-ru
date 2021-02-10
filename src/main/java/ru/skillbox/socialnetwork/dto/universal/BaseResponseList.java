package ru.skillbox.socialnetwork.dto.universal;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class BaseResponseList implements Response {
    private String error;
    private long timestamp;
    private long total;
    private long offset;
    private long perPage;
    private List<? extends Dto> data;

    public BaseResponseList(List<? extends Dto> data) {
        error = "string";
        timestamp = new Date().getTime();
        this.data = data;
    }

    public BaseResponseList(long total, long offset, long perPage, List<Dto> data) {
        error = "string";
        timestamp = new Date().getTime();
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }


}
