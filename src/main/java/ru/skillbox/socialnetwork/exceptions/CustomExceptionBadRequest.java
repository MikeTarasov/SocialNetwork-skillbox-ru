package ru.skillbox.socialnetwork.exceptions;

public class CustomExceptionBadRequest extends RuntimeException{
    public CustomExceptionBadRequest(String message) {
        super(message);
    }
}
