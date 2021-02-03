package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
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
    private LocalDateTime time;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @Transient
    @OneToMany(mappedBy = "postLike", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(value = LazyCollectionOption.EXTRA)
    private List<PostLike> postLike = new ArrayList<>();

    public Post(long id, LocalDateTime time, Person author, String title, String postText, int isBlocked,
                int isDeleted, List<PostComment> comments) {
        this.id = id;
        this.time = time;
        this.author = author;
        this.title = title;
        this.postText = postText;
        this.isBlocked = isBlocked;
        this.isDeleted = isDeleted;
        this.comments = comments;
    }

    public long getTimestamp() {
        return time.toInstant(ZoneOffset.of(String.valueOf(ZoneId.systemDefault()))).toEpochMilli();
    }
}
