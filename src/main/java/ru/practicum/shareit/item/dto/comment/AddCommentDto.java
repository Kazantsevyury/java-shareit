package ru.practicum.shareit.item.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {
    @NotBlank(message = "Пустой отзыв.")
    private String text;

}