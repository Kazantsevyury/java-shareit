package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentStorageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentStorage commentStorage;

    private User user;
    private Item item;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        entityManager.merge(user);
        item = new Item(1L, "Bike", "A nice bike", true, user, null);
        entityManager.merge(item);
    }

    @Test
    public void whenFindAllByItemId_thenReturnComments() {

        Comment comment1 = new Comment(null, "Great!", item, user, LocalDateTime.now());
        Comment comment2 = new Comment(null, "Awesome!", item, user, LocalDateTime.now().plusDays(1));
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        List<Comment> comments = commentStorage.findAllByItemId(item.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText).containsExactlyInAnyOrder("Great!", "Awesome!");
    }

}