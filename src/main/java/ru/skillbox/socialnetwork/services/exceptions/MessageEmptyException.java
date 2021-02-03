package ru.skillbox.socialnetwork.services.exceptions;

public class MessageEmptyException extends RuntimeException {
    public MessageEmptyException() {
        super("Can't send empty message!");
    }

    public MessageEmptyException(Long id){
        super("Can't set empty message! Message ID: " + id);
    }
}
