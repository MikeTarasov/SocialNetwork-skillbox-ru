package ru.skillbox.socialnetwork.exceptions;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(long id) {
        super("invalid message ID: " + id);
    }
}
