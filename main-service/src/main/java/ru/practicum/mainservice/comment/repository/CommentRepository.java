package ru.practicum.mainservice.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.shared.State;
import ru.practicum.mainservice.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    List<Comment> findAllByState(State published, PageRequest page);

    List<Comment> findAllByAuthorId(Long userId, PageRequest page);
}
