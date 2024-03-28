package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.EntityAlreadyExistsException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserCreateDto userCreateDto) {
        try {
            userStorage.findByEmail(userCreateDto.getEmail()).ifPresent(user -> {
                throw ExceptionFactory.entityAlreadyExists("Email уже используется другим пользователем.");
            });
            User user = UserMapper.INSTANCE.toUser(userCreateDto);
            user = userStorage.save(user);
            return UserMapper.INSTANCE.toUserDto(user);
        } catch (EntityAlreadyExistsException e) {
            incrementIdCounter();
            throw e;
        }
    }


    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().isEmpty()) {
            userStorage.findByEmail(userUpdateDto.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw ExceptionFactory.entityAlreadyExists("Email уже используется другим пользователем.");
                        }
                    });
        }

        UserMapper.INSTANCE.updateUserFromDto(userUpdateDto, user);
        user = userStorage.save(user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userStorage.findById(id).orElseThrow(() ->
                ExceptionFactory.userNotFoundException("Пользователь с ID " + id + " не найден"));
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public Collection<UserDto> findAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userStorage.existsById(id)) {
            throw ExceptionFactory.userNotFoundException("Пользователь с ID " + id + " не найден");
        }
        userStorage.deleteById(id);
    }

    @Transactional
    public void incrementIdCounter() {
        User temporaryUser = new User();
        userStorage.save(temporaryUser);

        userStorage.delete(temporaryUser);
    }

    @Override
    public User getPureUserById(Long userId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                ExceptionFactory.userNotFoundException("Пользователь с ID " + userId + " не найден"));
        return user;
    };

}