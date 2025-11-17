package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private Item item;
    private User booker;


    @BeforeEach
    void prepare() {
        owner = entityManager.persistAndFlush(User.builder()
                .name("owner")
                .email("owneremail@email.com")
                .build());

        item = entityManager.persistAndFlush(Item.builder()
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build());
        booker = entityManager.persistAndFlush(User.builder()
                .name("booker")
                .email("bookeremail@email.com")
                .build());
    }

    @Test
    void findAllByBookerIdAndStatus_whenFound_thenReturnBookingsList() {
        Booking booking = entityManager.persistAndFlush(Booking.builder()
                        .item(item)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .status(BookingStatus.WAITING)
                        .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.WAITING);

        assertFalse(bookingsFromBase.isEmpty());
        assertEquals(booking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStatus_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase1 = bookingRepository.findAllByBookerIdAndStatus(Long.MAX_VALUE, BookingStatus.WAITING);
        List<Booking> bookingsFromBase2 = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.APPROVED);

        assertTrue(bookingsFromBase1.isEmpty());
        assertTrue(bookingsFromBase2.isEmpty());
    }

    @Test
    void findAllByBookerId_whenFound_thenReturnBookingsList() {
        Booking booking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findAllByBookerId(booker.getId());

        assertFalse(bookingsFromBase.isEmpty());
        assertEquals(booking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findAllByBookerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findAllByBookerId(Long.MAX_VALUE);

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findCurrentBookingsByBookerId_whenFound_thenReturnBookingsList() {
        Booking currentBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(currentBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findCurrentBookingsByBookerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findCurrentBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findPastBookingsByBookerId_whenFound_thenReturnBookingsList() {
        Booking pastBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(pastBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findPastBookingsByBookerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findFutureBookingsByBookerId_whenFound_thenReturnBookingsList() {
        Booking futureBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findFutureBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(futureBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findFutureBookingsByBookerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findFutureBookingsByBookerId(booker.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findPastBookingsByBookerIdAndStatus_whenFound_thenReturnBookingsList() {
        Booking pastBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByBookerIdAndStatus(booker.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(1, bookingsFromBase.size());
        assertEquals(pastBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findPastBookingsByBookerIdAndStatus_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByBookerIdAndStatus(booker.getId(), LocalDateTime.now(), BookingStatus.WAITING);

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findAllByOwnerId_whenFound_thenReturnBookingsList() {
        Booking booking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findAllByOwnerId(owner.getId());

        assertFalse(bookingsFromBase.isEmpty());
        assertEquals(booking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findAllByOwnerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findAllByOwnerId(Long.MAX_VALUE);

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findAllByOwnerIdAndStatus_whenFound_thenReturnBookingsList() {
        Booking booking1 = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), BookingStatus.WAITING);

        assertEquals(1, bookingsFromBase.size());
        assertEquals(booking1.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findAllByOwnerIdAndStatus_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(), BookingStatus.REJECTED);

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findCurrentBookingsByOwnerId_whenFound_thenReturnBookingsList() {
        Booking currentBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findCurrentBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(currentBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findCurrentBookingsByOwnerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findCurrentBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findPastBookingsByOwnerId_whenFound_thenReturnBookingsList() {
        Booking pastBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(pastBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findPastBookingsByOwnerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findPastBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findFutureBookingsByOwnerId_whenFound_thenReturnBookingsList() {
        Booking futureBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findFutureBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertEquals(1, bookingsFromBase.size());
        assertEquals(futureBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findFutureBookingsByOwnerId_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findFutureBookingsByOwnerId(owner.getId(), LocalDateTime.now());

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findLastBookingsByItemIdsAndStatus_whenFound_thenReturnBookingsList() {
        Booking pastBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findLastBookingsByItemIdsAndStatus(List.of(item.getId()), LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(1, bookingsFromBase.size());
        assertEquals(pastBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findLastBookingsByItemIdsAndStatus_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findLastBookingsByItemIdsAndStatus(List.of(item.getId()), LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(bookingsFromBase.isEmpty());
    }

    @Test
    void findNextBookingsByItemIdsAndStatus_whenFound_thenReturnBookingsList() {
        Booking futureBooking = entityManager.persistAndFlush(Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build());

        List<Booking> bookingsFromBase = bookingRepository.findNextBookingsByItemIdsAndStatus(List.of(item.getId()), LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(1, bookingsFromBase.size());
        assertEquals(futureBooking.getId(), bookingsFromBase.get(0).getId());
    }

    @Test
    void findNextBookingsByItemIdsAndStatus_whenNotFound_thenReturnEmptyList() {
        List<Booking> bookingsFromBase = bookingRepository.findNextBookingsByItemIdsAndStatus(List.of(item.getId()), LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(bookingsFromBase.isEmpty());
    }
}
