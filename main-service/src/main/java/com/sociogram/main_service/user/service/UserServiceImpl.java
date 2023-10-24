package com.sociogram.main_service.user.service;

import com.sociogram.main_service.exception.DataConflictException;
import com.sociogram.main_service.exception.InvalidStatusException;
import com.sociogram.main_service.exception.UserNotFoundException;
import com.sociogram.main_service.user.dto.UserDto;
import com.sociogram.main_service.user.mapper.UserMapper;
import com.sociogram.main_service.user.model.User;
import com.sociogram.main_service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addNewUser(UserDto userDto) {
        if (userDto.getEmail().length() > 254) {
            throw new InvalidStatusException("Mail too long");
        }
        Optional<User> optionalUser = userRepository.findByName(userDto.getName());
        if (optionalUser.isPresent()) {
            throw new DataConflictException("Name already taken");
        }
        log.info("New user added: {}", userDto.getName());
        return UserMapper.toDtoUser(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        int page = 0;
        if (from != 0) {
            page = from / size;
        }
        Pageable pageable = PageRequest.of(page, size);
        if (ids == null) {
            log.info("Received users from page {} of size {}", page, size);
            return userRepository.findAll(pageable).stream().map(UserMapper::toDtoUser).collect(Collectors.toList());
        }
        log.info("Retrieved users by ID: {}", ids);
        return userRepository.findAllByIdIn(ids, pageable).stream().map(UserMapper::toDtoUser).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        userRepository.deleteById(id);
        log.info("User with ID: {} has been deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return UserMapper.toDtoUser(userRepository.findById(id).orElseThrow(() -> {
            log.error("Error when retrieving user by ID: User with ID {} not found", id);
            return new UserNotFoundException("User is not found");
        }));
    }
}
