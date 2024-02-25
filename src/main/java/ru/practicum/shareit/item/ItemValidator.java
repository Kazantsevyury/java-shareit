package ru.practicum.shareit.item;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.exceptions.InvalidDataException;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemValidator {

    public void validate(Item item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new InvalidDataException("Item name cannot be empty.");
        }
        if (item.getDescription() == null || item.getDescription().trim().isEmpty()) {
            throw new InvalidDataException("Description cannot be empty.");
        }
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new InvalidDataException("Item must be available.");
        }
    }
}