package ru.practicum.shareit.booking.dao.impl;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class BookingInMemoryStorage implements BookingStorage {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private Long currentId = Long.valueOf(0);

    @Override
    public Booking add(Booking booking) {
        booking.setId(++currentId);
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public void remove(Long id) {
        bookings.remove(id);
    }

    @Override
    public void update(Booking booking) {
        if (bookings.containsKey(booking.getId())) {
            bookings.put(booking.getId(), booking);
        }
    }

    @Override
    public Collection<Booking> findAll() {
        return bookings.values();
    }

    @Override
    public Booking findById(Long id) {
        return bookings.get(id);
    }
}