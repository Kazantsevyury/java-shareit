package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ValidateDateRangeValidatorTest {

    @InjectMocks
    private ValidateDateRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    private TestObject testObject;

    @BeforeEach
    public void setUp() {
        validator.initialize(TestObject.class.getAnnotation(ValidateDateRange.class));
        testObject = new TestObject();
    }

    @Test
    public void testIsValidWhenDateRangeIsValidThenReturnTrue() {
        testObject.setStartDate(LocalDateTime.now().plusDays(1));
        testObject.setEndDate(LocalDateTime.now().plusDays(2));
        assertTrue(validator.isValid(testObject, context));
    }

    @Test
    public void testIsValidWhenStartDateIsAfterEndDateThenReturnFalse() {
        testObject.setStartDate(LocalDateTime.now().plusDays(2));
        testObject.setEndDate(LocalDateTime.now().plusDays(1));
        assertFalse(validator.isValid(testObject, context));
    }

    @Test
    public void testIsValidWhenEndDateIsEqualToStartDateThenReturnFalse() {
        LocalDateTime sameDate = LocalDateTime.now().plusDays(1);
        testObject.setStartDate(sameDate);
        testObject.setEndDate(sameDate);
        assertFalse(validator.isValid(testObject, context));
    }

    @Test
    public void testIsValidWhenEndDateIsEqualToCurrentDateThenReturnFalse() {
        testObject.setStartDate(LocalDateTime.now().minusDays(1));
        testObject.setEndDate(LocalDateTime.now());
        assertFalse(validator.isValid(testObject, context));
    }

    @Test
    public void testIsValidWhenStartDateIsBeforeCurrentDateThenReturnFalse() {
        testObject.setStartDate(LocalDateTime.now().minusDays(1));
        testObject.setEndDate(LocalDateTime.now().plusDays(1));
        assertFalse(validator.isValid(testObject, context));
    }

    @Test
    public void testIsValidWhenStartOrEndFieldDoesNotExistThenReturnFalse() {
        assertFalse(validator.isValid(new Object(), context));
    }

    @Test
    public void testIsValidWhenExceptionThrownThenReturnFalse() {
        testObject.setStartDate(null);
        testObject.setEndDate(LocalDateTime.now().plusDays(1));
        assertFalse(validator.isValid(testObject, context));
    }

    // Helper class to simulate the object being validated
    @ValidateDateRange(start = "startDate", end = "endDate", message = "Invalid date range")
    private static class TestObject {
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }
}