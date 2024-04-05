package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddItemRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenDescriptionIsNotBlank_thenNoConstraintViolations() {
        // Arrange
        AddItemRequestDto dto = new AddItemRequestDto("This is a valid description.");

        // Act
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenDescriptionIsBlank_thenOneConstraintViolation() {
        // Arrange
        AddItemRequestDto dto = new AddItemRequestDto("");

        // Act
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым.", violations.iterator().next().getMessage());
    }

    @Test
    public void whenDescriptionIsNull_thenOneConstraintViolation() {
        // Arrange
        AddItemRequestDto dto = new AddItemRequestDto(null);

        // Act
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(dto);

        // Assert
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым.", violations.iterator().next().getMessage());
    }
}