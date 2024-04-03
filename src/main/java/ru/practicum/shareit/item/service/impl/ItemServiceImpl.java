package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ExceptionFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.GetItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;
    @Lazy
    private final UserService userService;
    @Lazy

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;

    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        UserDto ownerDTO = userService.findUserById(userId);
        Item item = itemMapper.toModel(itemDto);
        item.setOwner(userMapper.fromUserDto(ownerDTO));
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestService.getPureItemRequestById(itemDto.getRequestId());
            item.setRequest(request);
        }
        Item savedItem = itemStorage.save(item);
        ItemDto savedItemDto = itemMapper.toDto(savedItem);
        savedItemDto.setRequestId(itemDto.getRequestId());
        return savedItemDto;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw ExceptionFactory.accessDenied("User не является хозяином. ");
        }
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        Item updatedItem = itemStorage.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public GetItemDto findItemById(final Long userId, final Long itemId) {
        final Item item =  getPureItemById(itemId);
        List<Booking> itemBookings = bookingService.findAllByItemId(itemId);
        GetItemDto itemWithBookingDatesDto;
        if (item.getOwner().getId().equals(userId)) {
            itemWithBookingDatesDto = getItemWithBookings(item, itemBookings);
        } else {
            itemWithBookingDatesDto = itemMapper.toWithBookingsDto(item);
        }
        List<Comment> comments = commentStorage.findAllByItemId(item.getId());
        itemWithBookingDatesDto.getComments().addAll(commentMapper.toDtoList(comments));
        log.info("Получение вещи с id '{}'.", itemId);
        return itemWithBookingDatesDto;
    }

    private List<GetItemDto> getItemsWithBookingsAndComments(List<Item> items, List<Booking> bookings, List<Comment> itemsComments) {
        if (bookings.isEmpty()) {
            return itemMapper.toWithBookingsDtoList(items);
        }
        final Map<Long, List<Booking>> itemIdToBookings = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Comment>> itemIdToComments = itemsComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toList()));
        final List<GetItemDto> result = new ArrayList<>();
        for (Item item : items) {
            final Long itemId = item.getId();
            List<Booking> itemBookings = itemIdToBookings.computeIfAbsent(itemId, k -> new ArrayList<>());
            List<Comment> itemComments = itemIdToComments.computeIfAbsent(itemId, k -> new ArrayList<>());
            final GetItemDto itemWithBookingDatesDto = getItemWithBookings(item, itemBookings);
            itemWithBookingDatesDto.getComments().addAll(commentMapper.toDtoList(itemComments));
            result.add(itemWithBookingDatesDto);
        }
        return result;
    }


    @Override
    public List<GetItemDto> findAllItemsByUserId(Long userId) {
        userService.findUserById(userId);
        List<Item> items = itemStorage.findAllByOwnerIdOrderById(userId);
        final List<Long> itemIds = items.stream()
                .map(Item::getId).collect(Collectors.toList());
        final List<Booking> bookingFromIds = bookingService.findAllByItemIdIn(itemIds);

        List<Comment> itemsComments = commentStorage.findAllByItemIdIn(itemIds);
        final List<GetItemDto> itemsWithBookings =
                getItemsWithBookingsAndComments(items, bookingFromIds, itemsComments);
        return itemsWithBookings;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchAvailableItemsByText(text.toLowerCase())
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }




    @Override
    public void removeItem(Long itemId) {
        itemStorage.deleteById(itemId);
    }

    @Override
    public Item getPureItemById(Long id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Item", id));
        return item;
    }

    private GetItemDto getItemWithBookings(final Item item, final List<Booking> itemBookings) {
        final Optional<Booking> closestBooking = itemBookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart));
        final Optional<Booking> lastBooking = itemBookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) &&
                        booking.getStatus().equals(BookingStatus.APPROVED))
                .max(Comparator.comparing(Booking::getEnd));
        return itemMapper.toGetItemDto(item,
                bookingMapper.toShortDto(lastBooking.orElse(null)),
                bookingMapper.toShortDto(closestBooking.orElse(null)));

    }

}