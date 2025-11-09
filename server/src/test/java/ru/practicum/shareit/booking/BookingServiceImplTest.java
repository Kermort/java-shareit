package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import({BookingServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private Item item;
    private User owner;

    @BeforeEach
    void init() {
        booker = User.builder()
                .id(1L)
                .name("user")
                .email("email@email.com")
                .build();
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@email.com")
                .build();
        item = Item.builder()
                .id(1L)
                .owner(owner)
                .name("item")
                .description("description")
                .available(true)
                .build();
    }

    @Test
    void create_whenSuccess_thenReturnBookingDto() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.create(bookingRequestDto, booker.getId());

        assertNotNull(result);
        assertEquals(bookingRequestDto.getStart(), result.getStart());
        assertEquals(bookingRequestDto.getEnd(), result.getEnd());
        assertEquals(booker.getName(), result.getBooker().getName());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void create_whenBookerNotFound_thenThrowException() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingRequestDto, Long.MAX_VALUE));
    }

    @Test
    void create_whenItemNotFound_thenThrowException() {
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(Long.MAX_VALUE)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingRequestDto, booker.getId()));
    }

    @Test
    void create_whenItemIsUnavailable_thenThrowException() {
        item.setAvailable(false);
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingRequestDto, booker.getId()));
    }

    @Test
    void approve_whenAccept_thenReturnBookingDtoWithApprovedStatus() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approve_whenReject_thenReturnBookingDtoWithRejectedStatus() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void approve_whenUserIsNotOwner_thenThrowException() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAuthorizedException.class, () -> bookingService.approveBooking(booking.getId(), Long.MAX_VALUE, true));
    }

    @Test
    void findBookingByIdForOwnerOrBooker_whenUserIsOwner_thenReturnBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findBookingByIdForOwnerOrBooker(booking.getId(), owner.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void findBookingByIdForOwnerOrBooker_whenUserIsBooker_thenReturnBookingDto() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findBookingByIdForOwnerOrBooker(booking.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void findBookingByIdForOwnerOrBooker_whenUserIsNotOwnerOrBooker_thenThrowException() {
        Booking booking = Booking.builder()
                .id(1L)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotAuthorizedException.class,
                () -> bookingService.findBookingByIdForOwnerOrBooker(booking.getId(), Long.MAX_VALUE));
    }

    @Test
    void findBookingByIdForOwnerOrBooker_whenBookingNotFound_thenThrowException() {
        when(bookingRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.findBookingByIdForOwnerOrBooker(Long.MAX_VALUE, owner.getId()));
    }

    @Test
    void findBookingsByBookerAndState_whenStateIsAll_thenReturnAllBookings() {
        Booking b1 = Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        Item anotherItem = Item.builder()
                .owner(owner)
                .name("another item")
                .description("description")
                .available(true)
                .build();
        Booking b2 = Booking.builder()
                .item(anotherItem)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(booker.getId())).thenReturn(List.of(b1, b2));

        List<BookingDto> result = bookingService.findBookingsByBookerAndState(booker.getId(), BookingState.ALL);

        assertEquals(2, result.size());
    }

    @Test
    void findBookingsByBookerAndState_whenStateIsCurrent_thenReturnCurrentBookings() {
        Item anotherItem = Item.builder()
                .owner(owner)
                .name("another item")
                .description("description")
                .available(true)
                .build();
        Booking b2 = Booking.builder()
                .item(anotherItem)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .build();
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentBookingsByBookerId(eq(booker.getId()), any(LocalDateTime.class))).thenReturn(List.of(b2));

        List<BookingDto> result = bookingService.findBookingsByBookerAndState(booker.getId(), BookingState.CURRENT);

        assertEquals(1, result.size());
        assertEquals(b2.getItem().getName(), result.get(0).getItem().getName());
    }

    @Test
    void findBookingsByBookerAndState_whenBookerNotFound_thenThrowException() {
        when(userRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingsByBookerAndState(Long.MAX_VALUE, BookingState.ALL));
    }

    @Test
    void findBookingsByOwnerAndState_whenStateIsAll_thenReturnAllBookings() {
        Booking booking = Booking.builder()
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .build();
        Item anotherItem = Item.builder()
                .owner(owner)
                .name("another item")
                .description("description")
                .available(true)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of(item, anotherItem));
        when(bookingRepository.findAllByOwnerId(owner.getId())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findBookingsByOwnerAndState(owner.getId(), BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findBookingsByOwnerAndState_whenUserHasNoItems_thenThrowException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId())).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> bookingService.findBookingsByOwnerAndState(owner.getId(), BookingState.ALL));
    }
}
