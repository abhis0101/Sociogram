package com.sociogram.main_service.comments.mapper;

import com.sociogram.main_service.comments.dto.CommentResponseDto;
import com.sociogram.main_service.comments.dto.NewCommentDto;
import com.sociogram.main_service.comments.model.Comment;
import com.sociogram.main_service.event.model.Event;
import com.sociogram.main_service.user.model.User;
import lombok.experimental.UtilityClass;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment
                .builder()
                .id(null)
                .author(user)
                .text(newCommentDto.getText())
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto
                .builder()
                .author(comment.getAuthor().getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .id(comment.getId())
                .event(comment.getEvent().getId())
                .build();
    }

    public static List<CommentResponseDto> toListOfCommentResponseDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());
    }
}
