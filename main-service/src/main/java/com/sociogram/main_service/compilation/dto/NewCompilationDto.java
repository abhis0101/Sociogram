package com.sociogram.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@AllArgsConstructor
@Data
@ToString
@Builder
public class NewCompilationDto {
    private boolean pinned;
    @NotBlank(message = "Title cannot be empty")
    @Length(max = 50, message = "The title cannot be longer than 50 characters")
    private final String title;
    private final Set<Long> events;
}
