package ru.practicum.shareit.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testAddUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Hrothgar");
        userDto.setEmail("hrothgar@yandex.ru");

        given(userService.addUser(new UserCreateDto("Hrothgar", "hrothgar@yandex.ru"))).willReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hrothgar\",\"email\":\"hrothgar@yandex.ru\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hrothgar"))
                .andExpect(jsonPath("$.email").value("hrothgar@yandex.ru"));
    }
    @Test
    public void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto(1L, "Updated Hrothgar", "updatedhrothgar@yandex.ru");
        given(userService.updateUser(anyLong(), any(UserUpdateDto.class))).willReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Hrothgar\",\"email\":\"updatedhrothgar@yandex.ru\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Hrothgar"))
                .andExpect(jsonPath("$.email").value("updatedhrothgar@yandex.ru"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "Hrothgar", "hrothgar@yandex.ru"),
                new UserDto(2L, "Svyatoslav", "svyatoslav@yandex.ru")
        );
        given(userService.findAllUsers()).willReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Hrothgar"))
                .andExpect(jsonPath("$[1].name").value("Svyatoslav"));
    }

    @Test
    public void testGetUserById() throws Exception {
        UserDto userDto = new UserDto(1L, "Hrothgar", "hrothgar@yandex.ru");
        given(userService.findUserById(1L)).willReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hrothgar"))
                .andExpect(jsonPath("$.email").value("hrothgar@yandex.ru"));
    }
}
