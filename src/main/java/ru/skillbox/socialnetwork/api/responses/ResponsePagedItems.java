package ru.skillbox.socialnetwork.api.responses;

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
