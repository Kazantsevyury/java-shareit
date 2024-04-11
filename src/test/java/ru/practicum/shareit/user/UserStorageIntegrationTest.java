package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserStorageIntegrationTest {

    @Autowired
    private UserStorage userStorage;

    @Test
    public void testFindByEmail() {
        User user = User.builder()
                .name("Sigurd")
                .email("sigurd@yandex.ru")
                .build();
        userStorage.save(user);

        Optional<User> found = userStorage.findByEmail("sigurd@yandex.ru");
        assertTrue(found.isPresent());
        assertEquals("Sigurd", found.get().getName());
    }
}
