package com.sociogram.main_service.comments.controller;

import com.sociogram.main_service.comments.dto.CommentResponseDto;
import com.sociogram.main_service.comments.dto.NewCommentDto;
import com.sociogram.main_service.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(@PathVariable Long userId,
                                         @RequestParam Long eventId,
                                         @Valid @RequestBody NewCommentDto commentDto) {
        return commentService.addComment(userId, eventId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        commentService.deleteCommentById(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody NewCommentDto commentDto) {
        return commentService.updateComment(commentId, userId, commentDto);
    }

}
