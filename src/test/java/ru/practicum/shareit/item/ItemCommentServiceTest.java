package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.AddCommentDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.CommentServiceImpl;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemCommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ItemServiceImpl itemService;

    @Mock
    private BookingServiceImpl bookingService;

    @Mock
    private CommentServiceImpl commentService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;

    @Test
    void addCommentToItemShouldSuccessWhenConditionsMet() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(1L, "John Doe", "john.doe@example.com");
        Item item = new Item(1L, "Drill", "Powerful electric drill", true, user, null);
        Booking booking = new Booking(1L, item, user, BookingStatus.APPROVED, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        List<Booking> bookings = Collections.singletonList(booking);
        AddCommentDto commentDto = new AddCommentDto("Excellent!");
        Comment comment = new Comment(null, "Excellent!", item, user, LocalDateTime.now());
        CommentDto commentDtoResponse = new CommentDto(1L, "Excellent!", user.getName(), LocalDateTime.now());

        when(userService.getPureUserById(userId)).thenReturn(user);
        when(itemService.getPureItemById(itemId)).thenReturn(item);
        when(bookingService.findAllByItemIdAndBookerId(itemId, userId)).thenReturn(bookings);
        when(commentService.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDtoResponse);

        // Act
        CommentDto result = itemBookingFacade.addCommentToItem(userId, itemId, commentDto);

        // Assert
        assertNotNull(result);
        assertEquals("Excellent!", result.getText());
        verify(commentService).save(any(Comment.class));
        verify(commentMapper).toCommentDto(any(Comment.class));
    }
}


