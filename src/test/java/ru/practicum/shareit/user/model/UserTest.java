package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }

    @Test
    public void testDefaultConstructor() {
        assertTrue(user.getItems().isEmpty());
    }

    @Test
    public void testParameterizedConstructor() {
        User newUser = new User(1L, "John Doe", "john.doe@example.com");
        assertEquals(1L, newUser.getId());
        assertEquals("John Doe", newUser.getName());
        assertEquals("john.doe@example.com", newUser.getEmail());
        assertTrue(newUser.getItems().isEmpty());
    }

    @Test
    public void testSettersAndGetters() {
        user.setId(2L);
        user.setName("Jane Doe");
        user.setEmail("jane.doe@example.com");
        Item item = new Item();
        user.setItems(List.of(item));

        assertEquals(2L, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane.doe@example.com", user.getEmail());

    }

}
