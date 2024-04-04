package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemBookingFacadeImplIntegrationTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingServiceImpl bookingService;

    @MockBean
    private UserService userService;
    @MockBean
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemBookingFacadeImpl itemBookingFacade;

    private User user;
    private ItemDto itemDto;
    private AddBookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setAvailable(true);

        Item mockItem = new Item(); // Assuming there's a constructor or builder available
        mockItem.setId(itemDto.getId());
        mockItem.setAvailable(itemDto.getAvailable());
        // Set other required fields on mockItem...

        when(itemMapper.toModel(any(ItemDto.class))).thenReturn(mockItem); // Stubbing the method call

        bookingDto = new AddBookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void whenAddBookingWithUnavailableItem_thenThrowException() {
        // Setup
        itemDto.setAvailable(false);
        when(itemService.getPureItemById(any(Long.class))).thenReturn(new Item());

        // Action & Assertion
        assertThrows(Exception.class, () -> itemBookingFacade.addBooking(1L, bookingDto));
    }

}
