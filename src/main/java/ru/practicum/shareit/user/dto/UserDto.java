package ru.practicum.shareit.user.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private Long id;
    private String name;

    @NotEmpty(message = "Email не может быть пустым")
    @Email(message = "Неверно указан Email")
    private String email;
}
