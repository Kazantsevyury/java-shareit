package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items i ORDER BY ir.created DESC")
    List<ItemRequest> findAllRequests();

    @Query(value = "SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items i JOIN ir.requester r WHERE r.id != :requesterId ORDER BY ir.created DESC",
            countQuery = "SELECT COUNT(ir) FROM ItemRequest ir JOIN ir.requester r WHERE r.id != :requesterId")
    Page<ItemRequest> findAvailableRequests(@Param("requesterId") long requesterId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items i JOIN ir.requester r WHERE r.id = :requesterId ORDER BY ir.created DESC")
    List<ItemRequest> findRequestsFromUser(@Param("requesterId") Long requesterId);
}