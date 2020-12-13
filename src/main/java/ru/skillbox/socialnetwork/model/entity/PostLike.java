package ru.skillbox.socialnetwork.model.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;

@Entity
@Table(name = "post_like")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @CreatedDate
    @Column(name = "time", columnDefinition = "TIMESTAMP", nullable = false)
    private long time;

    @Column(name = "person_id")
    private long personId;

    @Column(name = "post_id")
    private long postId;


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

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
