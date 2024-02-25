package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Item {
    private long id;
    @NotBlank(message = "")
    private String name;
    @NotBlank(message = "")
    private String description;
    @NotBlank(message = "Необходимо указать доступность предмета.")
    private Boolean available;
    private Long owner;
    private String request;
}
