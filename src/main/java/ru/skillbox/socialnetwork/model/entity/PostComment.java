package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

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
    private LocalDateTime time;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "comment_text", columnDefinition = "VARCHAR(255)")
    private String commentText;

    @Column(name = "is_blocked")
    private int isBlocked;

    @Column(name = "is_deleted")
    private int isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "commentCL", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes;

    public PostComment(long id, LocalDateTime time, Long parentId, String commentText, int isBlocked, int isDeleted,
                       Person person, Post post) {
        this.id = id;
        this.time = time;
        this.parentId = parentId;
        this.commentText = commentText;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.person = person;
        this.post = post;
    }


    public PostComment(LocalDateTime time, Long parentId, String commentText, boolean isBlocked, boolean isDeleted,
                       Person person, Post post) {
        this.time = time;
        this.parentId = parentId;
        this.commentText = commentText;
        this.isBlocked = isBlocked ? 1 : 0;
        this.isDeleted = isDeleted ? 1 : 0;
        this.person = person;
        this.post = post;
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

    public long getTimestamp() {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}