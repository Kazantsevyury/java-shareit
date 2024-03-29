package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.service.UserService;

@Component
public class ItemDtoValidator implements Validator {
    private final UserService userService;

    public ItemDtoValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ItemDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ItemDto itemDto = (ItemDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.empty", "Имя не может быть пустым.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.empty", "Описание не может быть пустым.");

        if (itemDto.getAvailable() == null) {
            errors.rejectValue("available", "available.null", "Необходимо указать доступность предмета.");
        }

            // if (!userService.verifyUserExists(itemDto.getOwner())) {
         //   errors.rejectValue("owner", "owner.notfound", "Пользователь не найден.");
      //  }
    }
}

