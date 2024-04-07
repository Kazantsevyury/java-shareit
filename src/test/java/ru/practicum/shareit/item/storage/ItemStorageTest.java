package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder().name("Test User").email("testuser@gmail.com").build();
        testEntityManager.persist(user);
    }

    @AfterEach
    public void tearDown() {
        testEntityManager.clear();
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdWhenItemsExistThenReturnSortedItems() {
        List<Item> items = IntStream.range(0, 5)
                .mapToObj(i -> Item.builder().name("Item " + i).description("Description " + i).available(true).owner(user).build())
                .collect(Collectors.toList());

        items.forEach(testEntityManager::persist);

        List<Item> foundItems = itemStorage.findAllByOwnerIdOrderById(user.getId());

        assertThat(foundItems).isNotNull();
        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems).hasSize(5);
        assertThat(foundItems).isSortedAccordingTo((item1, item2) -> item1.getId().compareTo(item2.getId()));
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdWhenItemsDoNotExistThenReturnEmptyList() {
        List<Item> foundItems = itemStorage.findAllByOwnerIdOrderById(999L);

        assertThat(foundItems).isNotNull();
        assertThat(foundItems).isEmpty();
    }

    @Test
    public void testSearchAvailableItemsByTextWhenTextDoesNotMatchThenReturnEmptyList() {
        // Act
        List<Item> items = itemStorage.searchAvailableItemsByText("Nonexistent");

        // Assert
        assertTrue(items.isEmpty());
    }

    @Test
    public void testSearchAvailableItemsByTextWhenTextMatchesButItemNotAvailableThenReturnEmptyList() {
        // Act
        List<Item> items = itemStorage.searchAvailableItemsByText("Test Item 2");

        // Assert
        assertTrue(items.isEmpty());
    }
}