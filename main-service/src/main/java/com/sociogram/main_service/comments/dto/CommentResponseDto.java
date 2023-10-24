package com.sociogram.main_service.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;

    @NotNull
    private Long event;

    @NotNull
    private Long author;

    private String text;
}
