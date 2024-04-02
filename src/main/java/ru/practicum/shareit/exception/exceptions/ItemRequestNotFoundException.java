package ru.practicum.shareit.exception.exceptions;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
