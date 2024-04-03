package ru.practicum.shareit.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    public void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "Hrothgar", "hrothgar@yandex.ru");
        assertThat(json.write(dto)).hasJsonPathStringValue("@.email");
        assertThat(json.write(dto)).extractingJsonPathStringValue("@.email").isEqualTo("hrothgar@yandex.ru");
    }

    @Test
    public void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Hrothgar\",\"email\":\"hrothgar@yandex.ru\"}";
        assertThat(json.parse(content)).isEqualTo(new UserDto(1L, "Hrothgar", "hrothgar@yandex.ru"));
    }
}
