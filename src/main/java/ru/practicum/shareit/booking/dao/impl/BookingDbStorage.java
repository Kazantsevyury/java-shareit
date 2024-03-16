package ru.practicum.shareit.booking.dao.impl;

import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public class BookingDbStorage implements BookingStorage {

    @Override
    public Booking add(Booking booking) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void remove(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public void update(Booking booking) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Collection<Booking> findAll() {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }

    @Override
    public Booking findById(Long id) {
        throw new UnsupportedOperationException("Операция пока не поддерживается.");
    }
}
