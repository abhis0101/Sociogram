package com.sociogram.main_service.user.controller;

import com.sociogram.main_service.user.dto.UserDto;
import com.sociogram.main_service.user.service.UserService;
import com.sun.nio.sctp.IllegalReceiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody @Valid UserDto userDto) {
        return userService.addNewUser(userDto);

    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids, @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                  @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {

        if (size <= 0 || from < 0) {
            throw new IllegalReceiveException("Invalid parameter specified");
        }

        return userService.getUsers(ids, from, size);

    }
}
