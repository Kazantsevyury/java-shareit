package ru.practicum.shareit.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserCreateDto userCreateDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void init() {
        userCreateDto = new UserCreateDto("Yaroslav", "yaroslav@yandex.ru");
        updateDto = new UserUpdateDto("Vladimir", "vladimir@yandex.ru");
    }

    @Test
    void addUser_ShouldReturnUserWithId() {
        UserDto savedUser = userService.addUser(userCreateDto);

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getId(), greaterThan(0L));
        assertThat(savedUser.getName(), is(userCreateDto.getName()));
        assertThat(savedUser.getEmail(), is(userCreateDto.getEmail()));
    }

    @Test
    void updateUser_WithNameAndEmail_ShouldUpdateNameAndEmail() {
        UserDto savedUser = userService.addUser(userCreateDto);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getId(), is(savedUser.getId()));
        assertThat(updatedUser.getName(), is(updateDto.getName()));
        assertThat(updatedUser.getEmail(), is(updateDto.getEmail()));
    }

    @Test
    void updateUser_WithOnlyEmail_ShouldUpdateEmail() {
        updateDto.setName(null);
        UserDto savedUser = userService.addUser(userCreateDto);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getId(), is(savedUser.getId()));
        assertThat(updatedUser.getName(), is(savedUser.getName()));
        assertThat(updatedUser.getEmail(), is(updateDto.getEmail()));
    }

    @Test
    void updateUser_WithOnlyName_ShouldUpdateName() {
        updateDto.setEmail(null);
        UserDto savedUser = userService.addUser(userCreateDto);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getId(), is(savedUser.getId()));
        assertThat(updatedUser.getName(), is(updateDto.getName()));
        assertThat(updatedUser.getEmail(), is(savedUser.getEmail()));
    }

    @Test
    void updateUser_WithNullEmailAndName_ShouldNotUpdateAnyFields() {
        updateDto.setEmail(null);
        updateDto.setName(null);
        UserDto savedUser = userService.addUser(userCreateDto);
        UserDto updatedUser = userService.updateUser(savedUser.getId(), updateDto);

        assertThat(updatedUser.getId(), is(savedUser.getId()));
        assertThat(updatedUser.getName(), is(savedUser.getName()));
        assertThat(updatedUser.getEmail(), is(savedUser.getEmail()));
    }

    @Test
    void findUserById_UserFound_ShouldReturnUser() {
        UserDto savedUser = userService.addUser(userCreateDto);

        UserDto foundUser = userService.findUserById(savedUser.getId());

        assertThat(foundUser.getId(), is(savedUser.getId()));
        assertThat(foundUser.getName(), is(savedUser.getName()));
        assertThat(foundUser.getEmail(), is(savedUser.getEmail()));
    }

    @Test
    void findAllUsers_ShouldReturnListOfOne() {
        UserDto savedUser = userService.addUser(userCreateDto);

        List<UserDto> users = new ArrayList<>(userService.findAllUsers());

        assertThat(users, notNullValue());
        assertThat(users, hasSize(1));
        assertThat(users, contains(savedUser));
    }

    @Test
    void findAllUsers_ShouldReturnUserList() {
        UserDto savedUser = userService.addUser(userCreateDto);
        UserCreateDto userCreateDto2 = new UserCreateDto("Svyatoslav", "svyatoslav@yandex.ru");
        UserDto savedUser2 = userService.addUser(userCreateDto2);

        List<UserDto> users = new ArrayList<>(userService.findAllUsers());

        assertThat(users, notNullValue());
        assertThat(users, hasSize(2));
        assertThat(users, containsInAnyOrder(savedUser, savedUser2));
    }

    @Test
    void findAllUsers_WithNoUsers_ShouldReturnEmptyList() {
        List<UserDto> users = new ArrayList<>(userService.findAllUsers());

        assertThat(users, notNullValue());
        assertThat(users, is(empty()));
    }

    @Test
    void deleteUserById_UserExists_ShouldDeleteUser() {
        UserDto savedUser = userService.addUser(userCreateDto);

        userService.deleteUserById(savedUser.getId());
        List<UserDto> users = new ArrayList<>(userService.findAllUsers());

        assertThat(users, notNullValue());
        assertThat(users, is(empty()));
    }
}
