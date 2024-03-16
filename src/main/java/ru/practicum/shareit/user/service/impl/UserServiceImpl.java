package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserValidator;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Override
    public UserDto addUser(final UserDto userDto) {
        userValidator.validate(userMapper.userDtoToUser(userDto), false);
        //validateUserEmail(userDto.getEmail());
        final User user = userMapper.userDtoToUser(userDto);
        final User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return userMapper.userToUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto updatedUserDto) {
        log.debug("Начало обновления пользователя с id {}: {}", id, updatedUserDto);

        final User existingUser = userStorage.findById(id);
        if (existingUser == null) {
            log.warn("Пользователь с id {} не найден для обновления", id);
            throw ExceptionFactory.entityNotFound("Пользователь", id);
        }

        log.debug("Найден существующий пользователь для обновления: {}", existingUser);

        boolean isUpdated = false;


        if (updatedUserDto.getEmail() != null && !existingUser.getEmail().equals(updatedUserDto.getEmail())) {
            // Проверяем, существует ли пользователь с новым email, который не принадлежит текущему пользователю
            Optional<User> userByEmail = userStorage.findByEmail(updatedUserDto.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(id)) {
                log.warn("Попытка обновить email на уже существующий в системе, который принадлежит другому пользователю");
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже используется другим пользователем.");
            }

            log.debug("Обновление email с '{}' на '{}'", existingUser.getEmail(), updatedUserDto.getEmail());
            existingUser.setEmail(updatedUserDto.getEmail());
        }


        if (updatedUserDto.getName() != null) {
            log.debug("Обновление имени с '{}' на '{}'", existingUser.getName(), updatedUserDto.getName());
            existingUser.setName(updatedUserDto.getName());
            isUpdated = true;
        }

        if (isUpdated) {
            log.debug("Валидация обновленного пользователя: {}", existingUser);
            userValidator.validate(existingUser, true);
            userStorage.update(existingUser);
            log.info("Пользователь с id {} успешно обновлен: {}", id, existingUser);
        } else {
            log.info("Данные пользователя с id {} не были изменены. Обновление не требуется.", id);
        }

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
}
