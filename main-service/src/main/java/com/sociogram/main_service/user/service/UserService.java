package com.sociogram.main_service.user.service;

import com.sociogram.main_service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addNewUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void removeUser(Long id);

    UserDto getUserById(Long id);
}
