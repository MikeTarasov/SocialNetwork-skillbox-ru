package ru.skillbox.socialnetwork.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_like")
@Builder
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private long id;

    @CreatedDate
    @Column(name = "time", columnDefinition = "timestamp", nullable = false)
    private LocalDateTime time;

//    @Column(name = "person_id")
//    private long personId;
//
//    @Column(name = "post_id")
//    private long postId;

    @ManyToOne(fetch = FetchType.LAZY)//удалить если что
    @JoinColumn(name = "person_id")
    private Person personPL;

    @ManyToOne(fetch = FetchType.LAZY)//удалить если что
    @JoinColumn(name = "post_id")
    private Post postPL;
}
