package ru.practicum.mainservice.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.practicum.mainservice.shared.State;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String text;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Enumerated(EnumType.STRING)
    @Type(type = "ru.practicum.mainservice.shared.CustomEnum")
    private State state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!Objects.equals(id, comment.id)) return false;
        if (!Objects.equals(author, comment.author)) return false;
        if (!Objects.equals(event, comment.event)) return false;
        if (!Objects.equals(text, comment.text)) return false;
        if (!Objects.equals(createdOn, comment.createdOn)) return false;
        return state == comment.state;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}
