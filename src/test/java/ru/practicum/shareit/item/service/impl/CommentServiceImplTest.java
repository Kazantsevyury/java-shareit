package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private Item item;
    @Mock
    private UserDto userDto;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveWhenCommentPassedThenCommentSaved() {
        // Arrange
        Comment comment = new Comment(1L, "Nice item!", null, null, LocalDateTime.now());
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        // Act
        Comment savedComment = commentService.save(comment);

        // Assert
        assertEquals(comment, savedComment);
        verify(commentStorage, times(1)).save(comment);
    }

    @Test
    void testFindAllByItemIdWhenItemIdPassedThenCommentsReturned() {
        // Arrange
        Long itemId = 1L;
        List<Comment> comments = Collections.singletonList(new Comment(1L, "Nice item!", null, null, LocalDateTime.now()));
        when(commentStorage.findAllByItemId(itemId)).thenReturn(comments);

        // Act
        List<Comment> foundComments = commentService.findAllByItemId(itemId);

        // Assert
        assertEquals(1, foundComments.size());
        assertEquals(comments, foundComments);
        verify(commentStorage, times(1)).findAllByItemId(itemId);
    }
}
