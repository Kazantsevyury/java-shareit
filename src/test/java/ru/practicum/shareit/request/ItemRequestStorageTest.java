package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestStorageTest {

    @Autowired
    private ItemRequestStorage itemRequestStorage;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    private User loki;
    private User thor;
    private Item mjolnir;
    private ItemRequest lokiRequest1;
    private ItemRequest lokiRequest2;
    private ItemRequest thorRequest1;
    private ItemRequest thorRequest2;

    @BeforeAll
    public void init() {
        User userLoki = User.builder().name("Loki").email("loki@yandex.ru").build();
        loki = userStorage.save(userLoki);
        User userThor = User.builder().name("Thor").email("thor@yandex.ru").build();
        thor = userStorage.save(userThor);

        ItemRequest request1 = ItemRequest.builder().requester(loki).description("Viking ship").build();
        lokiRequest1 = itemRequestStorage.save(request1);
        Item item = Item.builder().owner(loki).available(true).name("Mjolnir").description("Thor's hammer")
                .request(lokiRequest1).build();
        mjolnir = itemStorage.save(item);
        lokiRequest1.addItem(mjolnir);
        lokiRequest1 = itemRequestStorage.save(request1);

        ItemRequest request2 = ItemRequest.builder().requester(thor).description("Asgardian feast").build();
        thorRequest1 = itemRequestStorage.save(request2);
        ItemRequest request3 = ItemRequest.builder().requester(thor).description("Odin's wisdom").build();
        thorRequest2 = itemRequestStorage.save(request3);
        ItemRequest request4 = ItemRequest.builder().requester(loki).description("Frost giant's lair").build();
        lokiRequest2 = itemRequestStorage.save(request4);
    }

    @AfterAll
    public void cleanDb() {
        itemStorage.deleteAll();
        itemRequestStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    public void findItemRequestsFromUser_ReturnRequestsWithoutItemsFromRequester() {
        List<ItemRequest> requests = itemRequestStorage.findRequestsFromUser(thor.getId());

        assertThat(requests.size(), is(2));
        assertThat(requests.get(0).getId(), is(thorRequest2.getId()));
        assertThat(requests.get(1).getId(), is(thorRequest1.getId()));
        assertThat(requests.get(0).getCreated(), is(notNullValue()));
        assertThat(requests.get(1).getCreated(), is(notNullValue()));
        assertThat(requests.get(0).getItems().size(), is(0));
        assertThat(requests.get(1).getItems().size(), is(0));
    }

    @Test
    public void findItemRequestsFromUser_ReturnRequestsWithItemsFromRequester() {
        List<ItemRequest> requests = itemRequestStorage.findRequestsFromUser(loki.getId());

        assertThat(requests.size(), is(2));
        assertThat(requests.get(0).getId(), is(lokiRequest2.getId()));
        assertThat(requests.get(1).getId(), is(lokiRequest1.getId()));
        assertThat(requests.get(0).getCreated(), is(notNullValue()));
        assertThat(requests.get(1).getCreated(), is(notNullValue()));
        assertThat(requests.get(0).getItems().size(), is(0));
        assertThat(requests.get(1).getItems().size(), is(1));
    }

}