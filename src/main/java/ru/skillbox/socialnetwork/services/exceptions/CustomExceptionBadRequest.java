package ru.skillbox.socialnetwork.services.exceptions;

public class CustomExceptionBadRequest extends RuntimeException{
    public CustomExceptionBadRequest(String message) {
        super(message);
    }
}
