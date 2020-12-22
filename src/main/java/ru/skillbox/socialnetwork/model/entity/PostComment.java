package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_comment")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "time", columnDefinition = "TIMESTAMP")
    private long time;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "comment_text", columnDefinition = "VARCHAR(255)")
    private String commentText;

    @Column(name = "is_blocked")
    private int isBlocked;

    @Column(name = "is_deleted")
    private int isDeleted;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Person author;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;



    public PostComment(long time, long parentId, String commentText, boolean isBlocked, boolean isDeleted, Person author) {
        this.time = time;
        this.parentId = parentId;
        this.commentText = commentText;
        this.isBlocked = isBlocked ? 1 : 0;
        this.isDeleted = isDeleted ? 1 : 0;
        this.author = author;
    }

    public boolean getIsBlocked() {
        return isBlocked == 1;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked ? 1 : 0;
    }

    public boolean getIsDeleted() {
        return isDeleted == 1;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted ? 1 : 0;
    }
}