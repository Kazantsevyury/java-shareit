package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemStorageIntegrationTest {

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveAndFindById() {
        User owner = new User();
        owner.setName("Odin");
        owner.setEmail("odin@yandex.ru");
        owner = entityManager.persistFlushFind(owner);

        Item item = new Item();
        item.setName("Gungnir");
        item.setDescription("Spear of Odin");
        item.setAvailable(true);
        item.setOwner(owner);
        item = entityManager.persistFlushFind(item);

        Item foundItem = itemStorage.findById(item.getId()).orElse(null);
        assertNotNull(foundItem);
        assertEquals("Gungnir", foundItem.getName());
        assertEquals("Spear of Odin", foundItem.getDescription());
    }
}
