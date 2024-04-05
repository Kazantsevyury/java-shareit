package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CommentStorageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentStorage commentStorage;

    @Test
    public void testFindAllByItemIdWhenCommentsExistThenReturnComments() {
        // Arrange
        User user = User.builder().name("Test User").email("testuser@gmail.com").build();
        Item item = Item.builder().name("Test Item").description("Test Description").available(true).owner(user).build();
        Comment comment1 = Comment.builder().text("Test Comment 1").item(item).author(user).created(LocalDateTime.now()).build();
        Comment comment2 = Comment.builder().text("Test Comment 2").item(item).author(user).created(LocalDateTime.now()).build();
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment1);
        entityManager.persist(comment2);

        // Act
        List<Comment> comments = commentStorage.findAllByItemId(item.getId());

        // Assert
        assertEquals(2, comments.size());
        assertTrue(comments.contains(comment1));
        assertTrue(comments.contains(comment2));
    }

    @Test
    public void testFindAllByItemIdWhenNoCommentsExistThenReturnEmptyList() {
        // Arrange
        User user = User.builder().name("Test User").email("testuser@gmail.com").build();
        Item item = Item.builder().name("Test Item").description("Test Description").available(true).owner(user).build();
        Comment comment = Comment.builder().text("Test Comment").item(item).author(user).created(LocalDateTime.now()).build();
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(comment);

        // Act
        List<Comment> comments = commentStorage.findAllByItemId(-1L);

        // Assert
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testFindAllByItemIdInWhenItemIdsEmptyThenReturnEmptyList() {
        // Arrange
        List<Long> itemIds = Collections.emptyList();

        // Act
        List<Comment> comments = commentStorage.findAllByItemIdIn(itemIds);

        // Assert
        assertThat(comments).isEmpty();
    }


}