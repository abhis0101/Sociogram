package com.sociogram.main_service.comments.repository;

import com.sociogram.main_service.comments.model.Comment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Long eventId, PageRequest request);

    Optional<Comment> findByEventIdAndAuthorId(Long eventId, Long userId);

    List<Comment> findTop10ByEventIdOrderByCreatedOnDesc(Long eventId);

}
