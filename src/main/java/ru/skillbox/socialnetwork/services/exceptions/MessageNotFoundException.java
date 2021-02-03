package ru.skillbox.socialnetwork.services.exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(long id) {
        super("invalid message ID: " + id);
    }
}
