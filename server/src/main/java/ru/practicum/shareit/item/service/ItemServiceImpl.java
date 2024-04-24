package ru.practicum.shareit.item.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.shared.OffsetPageRequest;
import ru.practicum.shareit.shared.exception.ItemUnavailableException;
import ru.practicum.shareit.shared.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto addItem(final Long userId, final ItemDto itemDto) {
        final User owner = getUser(userId);
        final Item item = itemMapper.toModel(itemDto);
        item.setOwner(owner);
        assignRequestToItem(itemDto, item);
        final Item addedItem = itemStorage.save(item);
        return itemMapper.toDto(addedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(final Long userId, final Long itemId, final ItemUpdateDto itemUpdateDto) {
        getUser(userId);
        final Item item = getItem(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("У пользователя с id '" + userId + "' не найдена вещь с id '" + itemId + "'.");
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
        final Item updatedItem = itemStorage.save(item);
        return itemMapper.toDto(updatedItem);
    }

    @Override
    public GetItemDto findItemById(final Long userId, final Long itemId) {
        getUser(userId);
        final Item item = getItem(itemId);
        List<Booking> itemBookings = bookingStorage.findAllByItemId(itemId);
        GetItemDto itemWithBookingDatesDto;
        if (item.getOwner().getId().equals(userId)) {
            itemWithBookingDatesDto = getItemWithBookings(item, itemBookings);
        } else {
            itemWithBookingDatesDto = itemMapper.toWithBookingsDto(item);
        }
        List<Comment> comments = commentStorage.findAllByItemId(item.getId());
        itemWithBookingDatesDto.getComments().addAll(commentMapper.toDtoList(comments));
        return itemWithBookingDatesDto;
    }

    @Override
    public List<GetItemDto> findAllItemsByUserId(final Long userId, Long from, Integer size) {
        getUser(userId);
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        final List<Item> items = itemStorage.findAllByOwnerIdOrderById(userId, pageRequest);
        final List<Long> itemIds = items.stream()
                .map(Item::getId).collect(Collectors.toList());
        final List<Booking> bookingFromIds = bookingStorage.findAllByItemIdIn(itemIds);
        List<Comment> itemsComments = commentStorage.findAllByItemIdIn(itemIds);
        final List<GetItemDto> itemsWithBookings =
                getItemsWithBookingsAndComments(items, bookingFromIds, itemsComments);
        return itemsWithBookings;
    }

    @Override
    public List<ItemDto> searchItems(final String text, Long from, Integer size) {
        String searchText = "%" + text.toLowerCase() + "%";
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        final Iterable<Item> searchResult = itemStorage.searchInTitleAndDescription(searchText, pageRequest);
        return itemMapper.toDtoList(Lists.newArrayList(searchResult));
    }

    @Override
    @Transactional
    public CommentDto addCommentToItem(final Long userId, final Long itemId, final AddCommentDto commentDto) {
        final User user = getUser(userId);
        final Item item = getItem(itemId);
        List<Booking> bookings = bookingStorage.findAllByItemIdAndBookerId(itemId, userId);
        checkIfUserCanAddComments(userId, itemId, bookings);
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        Comment savedComment = commentStorage.save(comment);
        return commentMapper.toDto(savedComment);
    }

    private List<GetItemDto> getItemsWithBookingsAndComments(List<Item> items, List<Booking> bookings, List<Comment> comments) {
        if (bookings.isEmpty()) {
            return itemMapper.toWithBookingsDtoList(items);
        }
        final Map<Long, List<Booking>> itemIdToBookings = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Comment>> itemIdToComments = comments.stream()
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

    private void checkIfUserCanAddComments(Long userId, Long itemId, List<Booking> bookings) {
        boolean isAbleToAddComment = bookings.stream()
                .anyMatch(booking -> booking.getBooker().getId().equals(userId) && booking.getEnd().isBefore(LocalDateTime.now())
                        && booking.getStatus().equals(BookingStatus.APPROVED));
        if (!isAbleToAddComment) {
            throw new ItemUnavailableException("Пользователь с id '" + userId + "' не брал в аренду вещь с id '" +
                    itemId + "'.");
        }
    }

    private User getUser(final long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден."));
    }

    private Item getItem(final long itemId) {
        return itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id '" + itemId + "' не найдена."));
    }

    private void assignRequestToItem(ItemDto itemDto, Item item) {
        Long requestId = itemDto.getRequestId();
        if (requestId != null && requestId > 0) {
            ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос с id '" + requestId + "' не найден."));
            itemRequest.addItem(item);
            item.setRequest(itemRequest);
        }
    }
}