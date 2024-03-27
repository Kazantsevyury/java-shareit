package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository <Booking, Long> {


    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP")
    boolean hasUserRentedItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status, org.springframework.data.domain.Pageable pageable);
    List<Booking> findAllByBookerIdAndEndBefore(Long userId, LocalDateTime endDateTime);

    List<Booking> findAllByBookerIdAndStartAfter(Long userId, LocalDateTime startDateTime);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId")
    List<Booking> findAllByItem_Owner_Id(@Param("userId") Long userId, Pageable pageable);


    List<Booking> findAllByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime endDateTime, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime startDateTime);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findAllByBookerId(Long bookerId);
    List<Booking> findAllByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);


}



