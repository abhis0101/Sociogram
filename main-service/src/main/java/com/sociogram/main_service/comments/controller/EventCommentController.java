package com.sociogram.main_service.comments.controller;

import com.sociogram.main_service.comments.dto.CommentResponseDto;
import com.sociogram.main_service.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class EventCommentController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAllCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return commentService.getAllCommentsByEventId(eventId, from, size);
    }

    @GetMapping("/last10")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> get10LatestCommentsByEventId(@PathVariable Long eventId) {
        return commentService.get10LatestCommentsByEventId(eventId);
    }
}

