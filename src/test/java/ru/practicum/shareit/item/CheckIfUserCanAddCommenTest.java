package ru.practicum.shareit.item;



 import org.junit.jupiter.api.Test;
 import org.mockito.InjectMocks;
 import org.springframework.boot.test.context.SpringBootTest;
 import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
 import static org.junit.jupiter.api.Assertions.assertThrows;

 import ru.practicum.shareit.booking.model.Booking;
 import ru.practicum.shareit.booking.enums.BookingStatus;
 import ru.practicum.shareit.exception.exceptions.ItemUnavailableException;
 import ru.practicum.shareit.item.model.Item;
 import ru.practicum.shareit.user.model.User;

 import java.time.LocalDateTime;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.List;

@SpringBootTest
public class CheckIfUserCanAddCommenTest {

    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;

    @Test
    void checkIfUserCanAddCommentsShouldNotThrowWhenUserHasRight() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "John Doe", "john.doe@example.com");
        Item item = new Item(itemId, "Drill", "Powerful electric drill", true, user, null);
        LocalDateTime pastTime = LocalDateTime.now().minusDays(5);
        LocalDateTime pastEnd = LocalDateTime.now().minusDays(4);
        Booking pastBooking = new Booking(1L, item, user, BookingStatus.APPROVED, pastTime, pastEnd);

        List<Booking> bookings = Arrays.asList(pastBooking);

        // Act & Assert
        assertDoesNotThrow(() -> itemBookingFacade.checkIfUserCanAddComments(userId, itemId, bookings));
    }

    @Test
    void shouldThrowWhenNoBookingsAtAll() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;

        // Act & Assert
        assertThrows(ItemUnavailableException.class, () ->
                        itemBookingFacade.checkIfUserCanAddComments(userId, itemId, Collections.emptyList()),
                "Пользователь с id '" + userId + "' не брал в аренду вещь с id '" + itemId + "'.");
    }

    @Test
    void shouldThrowWhenBookingNotApproved() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "John Doe", "john.doe@example.com");
        Item item = new Item(itemId, "Drill", "Powerful electric drill", true, user, null);
        LocalDateTime now = LocalDateTime.now();
        Booking rejectedBooking = new Booking(1L, item, user, BookingStatus.REJECTED, now.minusDays(1), now);

        List<Booking> bookings = Arrays.asList(rejectedBooking);

        // Act & Assert
        assertThrows(ItemUnavailableException.class, () ->
                        itemBookingFacade.checkIfUserCanAddComments(userId, itemId, bookings),
                "Пользователь с id '" + userId + "' не брал в аренду вещь с id '" + itemId + "'.");
    }

    @Test
    void shouldThrowWhenBookingIsStillActive() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        User user = new User(userId, "John Doe", "john.doe@example.com");
        Item item = new Item(itemId, "Drill", "Powerful electric drill", true, user, null);
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        Booking futureBooking = new Booking(1L, item, user, BookingStatus.APPROVED, LocalDateTime.now(), futureTime);

        List<Booking> bookings = Arrays.asList(futureBooking);

        // Act & Assert
        assertThrows(ItemUnavailableException.class, () ->
                        itemBookingFacade.checkIfUserCanAddComments(userId, itemId, bookings),
                "Пользователь с id '" + userId + "' не брал в аренду вещь с id '" + itemId + "'.");
    }
}
