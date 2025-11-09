package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.status = :status " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByBookerIdAndStatus(@Param("bookerId") Long bookerIdId,
                                             @Param("status") BookingStatus status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.end DESC ")
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                         @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "ORDER BY b.end DESC ")
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "ORDER BY b.end DESC ")

    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId,
                                                        @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < :now " +
            "AND b.status = :status " +
            "ORDER BY b.end DESC ")
    List<Booking> findPastBookingsByBookerIdAndStatus(@Param("bookerId") Long bookerId,
                                                      @Param("now") LocalDateTime now,
                                                      @Param("status") BookingStatus status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long bookerIdId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC ")
    List<Booking> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end >= :now " +
            "ORDER BY b.end DESC ")
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long bookerId,
                                               @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.end DESC ")

    List<Booking> findPastBookingsByOwnerId(@Param("ownerId") Long ownerId,
                                            @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.end DESC ")
    List<Booking> findFutureBookingsByOwnerId(@Param("ownerId") Long ownerId,
                                              @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.end < :now " +
            "AND b.status = :status ")
    List<Booking> findLastBookingsByItemIdsAndStatus(@Param("itemIds") List<Long> itemIds,
                                                     @Param("now") LocalDateTime now,
                                                     @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.start > :now " +
            "AND b.status = :status ")
    List<Booking> findNextBookingsByItemIdsAndStatus(@Param("itemIds") List<Long> itemIds,
                                                     @Param("now") LocalDateTime now,
                                                     @Param("status") BookingStatus status);
}
