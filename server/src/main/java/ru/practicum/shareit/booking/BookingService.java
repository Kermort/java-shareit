package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto dto, Long userId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto findBookingByIdForOwnerOrBooker(Long bookingId, Long userId);

    List<BookingDto> findBookingsByBookerAndState(Long userId, BookingState state);

    List<BookingDto> findBookingsByOwnerAndState(Long userId, BookingState state);
}
