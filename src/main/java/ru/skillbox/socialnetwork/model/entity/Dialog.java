package ru.skillbox.socialnetwork.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "dialog")
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Person owner;

    @Column(name = "unread_count")
    private int unreadCount;

    @Column(name = "is_deleted")
    private int isDeleted;

    @Column(name = "invite_code", columnDefinition = "varchar(255)")
    private String inviteCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        return id.equals(dialog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
