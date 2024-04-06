package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testIncrementIdCounterWhenIdCounterIsIncrementedThenSaveAndDeleteMethodsAreCalled() {
        // Arrange
        User temporaryUser = new User();

        // Act
        userService.incrementIdCounter();

        // Assert
        Mockito.verify(userStorage, times(1)).save(temporaryUser);
        Mockito.verify(userStorage, times(1)).delete(temporaryUser);
    }

    @Test
    public void testIncrementIdCounterWhenSaveOperationThrowsExceptionThenExceptionIsThrown() {
        // Arrange
        User temporaryUser = new User();
        Mockito.doThrow(new RuntimeException()).when(userStorage).save(temporaryUser);

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> userService.incrementIdCounter());
    }
}