package com.sociogram.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;

@Data
@ToString
@Builder
@AllArgsConstructor
public class UpdateCompilationRequest {
    private final Boolean pinned;
    @Length(min = 1, max = 50)
    private final String title;
    private final HashSet<Long> events;
}
