package ru.skillbox.socialnetwork.services.exceptions;

public class DialogNotFoundException extends RuntimeException {
    public DialogNotFoundException(long id) {
        super("invalid dialog ID: " + id);
    }
}
