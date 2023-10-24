package com.sociogram.main_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String name;

    private Long id;

    @NotBlank
    private String email;
}
