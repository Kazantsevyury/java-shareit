package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock private UserService userService;
    @Mock
    private ItemRequestStorage itemRequestStorage;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private AddItemRequestDto addItemRequestDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        // Adjusted to match the available User constructor
        user = new User(1L, "John Doe", "john.doe@example.com");

        // Adjusted to match the available ItemRequest constructor
        // Assuming the LocalDateTime is not needed in the constructor based on the error message
        itemRequest = new ItemRequest(1L, "Need a drill", user);

        addItemRequestDto = new AddItemRequestDto("Need a drill");
        itemRequestDto = new ItemRequestDto(1L, "Need a drill", LocalDateTime.now(), Collections.emptyList());
    }

    @Test
    void testAddNewItemRequestWhenValidThenItemRequestAdded() {
        when(userService.findUserById(anyLong())).thenReturn(null);
        when(userService.getPureUserById(anyLong())).thenReturn(user);
        when(itemRequestMapper.toModel(any(AddItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.addNewItemRequest(user.getId(), addItemRequestDto);

        verify(userService, times(1)).findUserById(user.getId());
        verify(userService, times(1)).getPureUserById(user.getId());
        verify(itemRequestMapper, times(1)).toModel(addItemRequestDto);
        verify(itemRequestStorage, times(1)).save(itemRequest);
        verify(itemRequestMapper, times(1)).toDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequestDto, result);
    }

    @Test
    void testGetAllItemRequestsFromUserWhenValidThenAllItemRequestsRetrieved() {
        when(userService.findUserById(anyLong())).thenReturn(null);
        when(itemRequestStorage.findRequestsFromUser(anyLong())).thenReturn(Collections.singletonList(itemRequest));
        when(itemRequestMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(itemRequestDto));

        List<ItemRequestDto> result = itemRequestService.getAllItemRequestsFromUser(user.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(itemRequestStorage, times(1)).findRequestsFromUser(user.getId());
        verify(itemRequestMapper, times(1)).toDtoList(Collections.singletonList(itemRequest));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    void testGetAvailableItemRequestsWhenValidThenAvailableItemRequestsRetrieved() {
        Page<ItemRequest> page = new PageImpl<>(Collections.singletonList(itemRequest));
        when(userService.findUserById(anyLong())).thenReturn(null);
        when(itemRequestStorage.findAvailableRequests(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(itemRequestMapper.toDtoList(anyList())).thenReturn(Collections.singletonList(itemRequestDto));

        List<ItemRequestDto> result = itemRequestService.getAvailableItemRequests(user.getId(), 0L, 10);

        verify(userService, times(1)).findUserById(user.getId());
        verify(itemRequestStorage, times(1)).findAvailableRequests(eq(user.getId()), any(PageRequest.class));
        verify(itemRequestMapper, times(1)).toDtoList(page.getContent());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    void testGetItemRequestByIdWhenValidThenItemRequestRetrieved() {
        when(userService.findUserById(anyLong())).thenReturn(null);
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        verify(userService, times(1)).findUserById(user.getId());
        verify(itemRequestStorage, times(1)).findById(itemRequest.getId());
        verify(itemRequestMapper, times(1)).toDto(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequestDto, result);
    }

    @Test
    void testGetItemRequestByIdWhenNotFoundThenThrowException() {
        when(userService.findUserById(anyLong())).thenReturn(null);
        when(itemRequestStorage.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()));

        verify(userService, times(1)).findUserById(user.getId());
        verify(itemRequestStorage, times(1)).findById(itemRequest.getId());
    }

    @Test
    void testGetPureItemRequestByIdWhenValidThenPureItemRequestRetrieved() {
        when(itemRequestStorage.getReferenceById(anyLong())).thenReturn(itemRequest);

        ItemRequest result = itemRequestService.getPureItemRequestById(itemRequest.getId());

        verify(itemRequestStorage, times(1)).getReferenceById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest, result);
    }
}