package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @Column(name = "time", columnDefinition = "TIMESTAMP")
    private long time;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "author_id")
    private Person author;

    @Column(columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "post_text", columnDefinition = "VARCHAR(2048)")
    private String postText;

    @Column(name = "is_blocked")
    private int isBlocked;

    @Column(name = "is_deleted")
    private int isDeleted;


//    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
//            CascadeType.DETACH, CascadeType.REFRESH})
//    private List<Comment> comments;


    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
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
