package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidateDateRangeTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenDatesAreValid_thenNoConstraintViolations() {
        TestObject obj = new TestObject(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
        assertEquals(0, violations.size());
    }

    @Test
    public void whenStartDateIsAfterEndDate_thenConstraintViolations() {
        TestObject obj = new TestObject(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));
        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
        assertEquals(1, violations.size());
    }

    @Test
    public void whenEndDateIsEqualToCurrentDate_thenConstraintViolations() {
        TestObject obj = new TestObject(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
        assertEquals(1, violations.size());
    }

    @Test
    public void whenStartDateIsBeforeCurrentDate_thenConstraintViolations() {
        TestObject obj = new TestObject(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        Set<ConstraintViolation<TestObject>> violations = validator.validate(obj);
        assertEquals(1, violations.size());
    }

    @ValidateDateRange(start = "startDate", end = "endDate", message = "Invalid date range")
    private static class TestObject {
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public TestObject(LocalDateTime startDate, LocalDateTime endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

    }
}
