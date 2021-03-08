package ru.skillbox.socialnetwork.api.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.socialnetwork.model.entities.Message;
import ru.skillbox.socialnetwork.model.entities.Person;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntityResponse {

    private long id;

    @JsonProperty("time")
    private long timestamp;

    @JsonProperty("author_id")
    private long authorId;

    @JsonProperty("recipient")
    private PersonEntityResponse recipient;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private String readStatus;

    @JsonProperty("isSentByMe")
    private boolean isSentByMe;

    public MessageEntityResponse(Message message, long currentPersonId) {
        Person author = message.getAuthor();
        Person recipient = author.getId() == currentPersonId ? message.getRecipient() : author;
        id = message.getId();
        authorId = author.getId();
        isSentByMe = authorId == currentPersonId;
        messageText = message.getText();
        timestamp = message.getTimestamp();
        readStatus = message.getReadStatus();
        this.recipient = new PersonEntityResponse(recipient);
    }
}