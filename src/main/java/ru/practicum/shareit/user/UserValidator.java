package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.exception.exceptions.InvalidDataException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    private final UserStorage userStorage;
    private final Pattern emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    @Autowired
    public UserValidator(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void validate(User user, boolean isUpdate) {
        logger.debug("Начало валидации пользователя: {}", user);

        if (user.getName() == null || user.getName().isEmpty()) {
            logger.warn("Имя пользователя пусто или null");
            throw new InvalidDataException("Имя пользователя не может быть пустым или null.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            logger.warn("Email пользователя пуст или null");
            throw new InvalidDataException("Email пользователя не может быть пустым или null.");
        }
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            logger.warn("Email пользователя '{}' не соответствует формату.", user.getEmail());
            throw new InvalidDataException("Email пользователя не соответствует формату.");
        }

        Optional<User> existingUserByEmail = userStorage.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent() && (!isUpdate || !existingUserByEmail.get().getId().equals(user.getId()))) {
            logger.warn("Пользователь с email '{}' уже существует.", user.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует.");
        }

        logger.debug("Валидация пользователя '{}' успешно завершена.", user.getEmail());
    }

    public void verifyUserExists(Long userId) {
        logger.debug("Проверка существования пользователя с ID: {}", userId);

        if (!userStorage.existsById(userId)) {
            logger.warn("Пользователь с ID: {} не найден.", userId);
            throw ExceptionFactory.userNotFoundException("Пользователь с ID: " + userId + " не найден.");
        }

        logger.debug("Пользователь с ID: {} успешно найден.", userId);
    }
}
