package com.sociogram.main_service.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {

    @NotBlank(message = "Не может быть пустым")
    @Size(min = 10, max = 2000, message = "Размер от 10 до 20000")
    private String text;

}
