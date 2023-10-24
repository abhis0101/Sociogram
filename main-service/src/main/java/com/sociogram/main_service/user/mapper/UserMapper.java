package com.sociogram.main_service.user.mapper;

import com.sociogram.main_service.user.dto.UserDto;
import com.sociogram.main_service.user.dto.UserShortDto;
import com.sociogram.main_service.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toDtoUser(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toDtoShortUser(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
