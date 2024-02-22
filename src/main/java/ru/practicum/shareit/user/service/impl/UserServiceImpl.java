package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(final UserDto userDto) {
        validateUserEmail(userDto.getEmail());
        final User user = userMapper.userDtoToUser(userDto);
        final User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return userMapper.userToUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto updatedUserDto) {
        final User existingUser = userStorage.findById(id);

        if (existingUser == null) {
            throw ExceptionFactory.entityNotFound("Пользователь", id);
        }

        if (updatedUserDto.getEmail() != null && !existingUser.getEmail().equals(updatedUserDto.getEmail())) {
            validateUserEmail(updatedUserDto.getEmail());
        }

        if (updatedUserDto.getName() != null) {
            existingUser.setName(updatedUserDto.getName());
        }
        if (updatedUserDto.getEmail() != null) {
            existingUser.setEmail(updatedUserDto.getEmail());
        }

        userStorage.update(existingUser);

        log.info("Обновление пользователя с id {}: {}", id, existingUser);
        return userMapper.userToUserDto(existingUser);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAll().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public UserDto getUserById(Long userId) {
        final User user = userStorage.findById(userId);
        if (user == null) {
            throw ExceptionFactory.entityNotFound("Пользователь", userId);
        }
        log.info("Пользователь с id {} найден.", userId);
        return userMapper.userToUserDto(user);
    }

    @Override
    public void removeUser(Long userId) {
        final User user = userStorage.findById(userId);
        if (user == null) {
            throw ExceptionFactory.entityNotFound("Пользователь", userId);
        }
        userStorage.remove(userId);
        log.info("Пользователь с id {} удален.", userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return userStorage.findById(userId) != null;
    }

    private void validateUserEmail(String email) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
        if (emailExists) {
            throw ExceptionFactory.entityAlreadyExists("Пользователь с таким email уже существует: " + email);
        }
    }
}
