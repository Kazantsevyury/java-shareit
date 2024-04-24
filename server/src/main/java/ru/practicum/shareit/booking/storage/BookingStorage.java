package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.id = :bookingId")
    Optional<Booking> findBookingById(@Param("bookingId") Long bookingId);

    @Query("SELECT b FROM Booking b JOIN b.item i JOIN FETCH b.booker u WHERE i.id = :itemId")
    List<Booking> findAllByItemId(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.id = :itemId AND u.id = :bookerId")
    List<Booking> findAllByItemIdAndBookerId(@Param("itemId") Long itemId, @Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.id IN :itemIds")
    List<Booking> findAllByItemIdIn(@Param("itemIds") Collection<Long> itemIds);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = :ownerId AND b.start <= :startBefore AND b.end >= :endAfter ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("startBefore") LocalDateTime startBefore,
                                               @Param("endAfter") LocalDateTime endAfter, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = :ownerId AND b.end <= :endBefore ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("endBefore") LocalDateTime endBefore, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = :ownerId AND b.start >= :startAfter ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("startAfter") LocalDateTime startAfter, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE i.owner.id = :ownerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = :bookerId AND b.start <= :startBefore AND b.end >= :endAfter ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("startBefore") LocalDateTime startBefore,
                                                @Param("endAfter") LocalDateTime endAfter, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = :bookerId AND b.end <= :endBefore ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("endBefore") LocalDateTime endBefore, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = :bookerId AND b.start >= :startAfter ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("startAfter") LocalDateTime startAfter, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b JOIN FETCH b.item i JOIN FETCH b.booker u WHERE b.booker.id = :bookerId AND b.status = :status ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerIdAndStatus(@Param("bookerId") Long bookerId, @Param("status") BookingStatus status, Pageable pageable);
}
