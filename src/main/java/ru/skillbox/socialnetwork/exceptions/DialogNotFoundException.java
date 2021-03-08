package ru.skillbox.socialnetwork.exceptions;

public class DialogNotFoundException extends RuntimeException {
    public DialogNotFoundException(long id) {
        super("invalid dialog ID: " + id);
    }
}
