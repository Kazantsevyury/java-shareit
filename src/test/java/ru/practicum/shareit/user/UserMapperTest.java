package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void shouldMapToUser() {
        UserCreateDto dto = new UserCreateDto("Test User", "test@example.com");
        User user = mapper.toUser(dto);

        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
        assertThat(user.getId()).isNull();
    }

    @Test
    public void shouldMapToUserDto() {
        User user = new User(1L, "Test User", "test@example.com");
        UserDto dto = mapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getName()).isEqualTo(user.getName());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldUpdateUserFromDto() {
        UserUpdateDto dto = new UserUpdateDto("Updated Name", null);
        User user = new User(1L, "Test User", "test@example.com");

        mapper.updateUserFromDto(dto, user);

        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo("test@example.com"); // Email should remain unchanged
    }

    @Test
    public void shouldMapFromUserDto() {
        UserDto dto = new UserDto(1L, "Test User", "test@example.com");
        User user = mapper.fromUserDto(dto);

        assertThat(user.getId()).isEqualTo(dto.getId());
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getEmail()).isEqualTo(dto.getEmail());
    }
}
