package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    @Query(value = "SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items i JOIN ir.requester r WHERE r.id != ?1 ORDER BY ir.created DESC ",
            countQuery = "SELECT COUNT(ir) FROM ItemRequest ir LEFT JOIN ir.items i JOIN ir.requester r WHERE r.id != ?1 GROUP BY ir.created ORDER BY ir.created DESC ")
    Page<ItemRequest> findAvailableRequests(long requesterId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items i JOIN ir.requester r WHERE r.id = ?1 ORDER BY ir.created DESC ")
    List<ItemRequest> findRequestsFromUser(Long requesterId);
}
