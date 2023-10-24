package com.sociogram.main_service.comments.service;

import com.sociogram.main_service.comments.dto.CommentResponseDto;
import com.sociogram.main_service.comments.dto.NewCommentDto;
import com.sociogram.main_service.comments.mapper.CommentMapper;
import com.sociogram.main_service.comments.model.Comment;
import com.sociogram.main_service.comments.repository.CommentRepository;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.event.model.RequestStatus;
import com.sociogram.main_service.event.model.State;
import com.sociogram.main_service.event.repository.EventRepository;
import com.sociogram.main_service.exception.DataConflictException;
import com.sociogram.main_service.exception.EntityNotFoundException;
import com.sociogram.main_service.exception.UserNotFoundException;
import com.sociogram.main_service.requests.model.ParticipationRequest;
import com.sociogram.main_service.requests.repository.ParticipationRequestRepository;
import com.sociogram.main_service.user.model.User;
import com.sociogram.main_service.user.repository.UserRepository;
import com.sun.nio.sctp.IllegalReceiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequest;

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Event event = getEventById(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new DataConflictException("Event not published");
        }

        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            List<ParticipationRequest> requests = participationRequest.findAllByEventIdAndStatusAndRequesterId(eventId, RequestStatus.CONFIRMED, userId);
            if (requests.isEmpty()) {
                throw new DataConflictException("You need to be a participant or organizer");
            }
        }
        Optional<Comment> foundComment = commentRepository.findByEventIdAndAuthorId(eventId, userId);
        if (foundComment.isPresent()) {
            throw new DataConflictException("You can only leave one same comment");
        }
        log.info("Comment saved");
        return CommentMapper.toCommentResponseDto(commentRepository.save(CommentMapper.toComment(commentDto, user, event)));
    }

    @Override
    @Transactional
    public void deleteCommentById(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        checkIfUserIsTheAuthor(comment.getAuthor().getId(), userId);
        commentRepository.deleteById(commentId);
        log.info("Comment has been deleted");
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long userId, NewCommentDto commentDto) {
        Comment comment = getCommentById(commentId);

        checkIfUserIsTheAuthor(comment.getAuthor().getId(), userId);

        String newText = commentDto.getText();
        if (StringUtils.hasLength(newText)) {
            comment.setText(newText);
        }
        log.info("Comment updated");
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Invalid parameter specified");
        }
        getEventById(eventId);
        PageRequest pageRequest = PageRequest.of(from, size);
        return CommentMapper.toListOfCommentResponseDto(commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId, pageRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> get10LatestCommentsByEventId(Long eventId) {
        getEventById(eventId);
        return CommentMapper.toListOfCommentResponseDto(commentRepository.findTop10ByEventIdOrderByCreatedOnDesc(eventId));
    }

    private void checkIfUserIsTheAuthor(Long authorId, Long userId) {
        if (!Objects.equals(authorId, userId)) {
            throw new DataConflictException("Author not found");
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new UserNotFoundException("Comment not found"));

    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }
}
