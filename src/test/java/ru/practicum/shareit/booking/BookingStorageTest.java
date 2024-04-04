package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.OffsetPageRequest;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingStorageTest {

    @Autowired
    private BookingStorage bookingStorage;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    private Item savedItem1;

    private Item savedItem2;

    private User savedUser1;

    private User savedUser2;

    private Booking savedBooking1;

    private OffsetPageRequest pageRequest;

    @BeforeAll
    void init() {
        User user1 = createUser(1L);
        savedUser1 = userStorage.save(user1);
        User user2 = createUser(2L);
        savedUser2 = userStorage.save(user2);

        Item item1 = createItem(1L);
        item1.setOwner(savedUser1);
        savedItem1 = itemStorage.save(item1);
        Item item2 = createItem(2L);
        item2.setOwner(savedUser1);
        savedItem2 = itemStorage.save(item2);

        Booking booking1 = createBooking(1L);
        booking1.setItem(savedItem1);
        booking1.setBooker(savedUser2);
        booking1.setStart(now().minusDays(5));
        booking1.setEnd(now().minusDays(1));
        savedBooking1 = bookingStorage.save(booking1);

        Booking booking2 = createBooking(2L);
        booking2.setItem(savedItem1);
        booking2.setBooker(savedUser2);
        booking2.setStart(now().minusDays(1));

        Booking booking3 = createBooking(3L);
        booking3.setItem(savedItem2);
        booking3.setBooker(savedUser1);
        pageRequest = OffsetPageRequest.of(0L, 1);
    }

    @AfterAll
    public void cleanDb() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void findBookingById_BookingNotFound_ShouldReturnEmptyOptional() {
        Optional<Booking> optionalBooking = bookingStorage.findBookingById(999L);

        assertTrue(optionalBooking.isEmpty());
    }

    @Test
    void findAllByItemId_NoBookings_ShouldReturnEmptyList() {

        List<Booking> bookings = bookingStorage.findAllByItemId(999L);

        assertThat(bookings, notNullValue());
        assertThat(bookings, emptyIterable());
    }


    @Test
    void findAllByItemIdAndBookerId_WhenItemsNotBelongToBooker_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingStorage.findAllByItemIdAndBookerId(savedItem1.getId(), savedUser1.getId());

        assertThat(bookings, notNullValue());
        assertThat(bookings, emptyIterable());
    }


    @Test
    void findAllByItemOwnerId_OwnerNotFoundWithPageable_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingStorage.findAllByItemOwnerId(999L, pageRequest);

        assertThat(bookings, notNullValue());
        assertThat(bookings, emptyIterable());
    }

    @Test
    void findBookingById_ShouldReturnBooking_WhenIdIsProvided() {
        Optional<Booking> foundBookingOpt = bookingStorage.findBookingById(savedBooking1.getId());

        assertTrue(foundBookingOpt.isPresent(), "Booking должен быть найден.");
        Booking foundBooking = foundBookingOpt.get();
        assertEquals(savedBooking1.getId(), foundBooking.getId(), "ID бронирования должны совпадать.");
    }
    @Test
    void findAllByItemId_ShouldReturnBookings_WhenItemIdIsProvided() {
        List<Booking> bookings = bookingStorage.findAllByItemId(savedItem1.getId());

        assertThat(bookings, not(empty()));
        assertThat("Все бронирования должны принадлежать одному предмету.",
                bookings.stream().allMatch(booking -> booking.getItem().getId().equals(savedItem1.getId())),
                is(true));
    }


    private Item createItem(Long id) {
        return Item.builder()
                .name("name" + id)
                .description("description" + id)
                .available(true)
                .build();
    }

    private User createUser(Long id) {
        return User.builder()
                .name("name" + id)
                .email("email" + id + "@mail.com")
                .build();
    }

    private Booking createBooking(Long id) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .start(now().plusDays(id))
                .end(now().plusDays(5 + id))
                .build();
    }
    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }


}