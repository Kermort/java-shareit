package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("SELECT ir FROM ItemRequest ir " +
           "WHERE ir.requestor.id = :requestorId " +
           "ORDER BY ir.created DESC ")
    List<ItemRequest> findByRequestorId(@Param("requestorId") Long requestorId);

    @Query("SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requestor.id != :userId " +
            "ORDER BY ir.created DESC ")
    List<ItemRequest> findAllByOtherUsers(@Param("userId") Long userId);
}
