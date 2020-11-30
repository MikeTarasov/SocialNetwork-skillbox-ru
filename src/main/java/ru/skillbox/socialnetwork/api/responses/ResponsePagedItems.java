package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
// universal response to wrap various specific multiple responses with error message, timestamps & paging
public class ResponsePagedItems {

    private String error;
    private long timestamp;
    private int total;
    private int offset;
    private int perPage;
    private Object data;

    public ResponsePagedItems(String error, int total, int offset, int perPage, Object data) {
        this.error = error;
        this.timestamp = System.currentTimeMillis();
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}
