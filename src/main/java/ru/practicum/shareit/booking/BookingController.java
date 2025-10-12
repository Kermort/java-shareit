package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingRequestDto dto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@Valid @PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingByIdForOwnerOrBooker(@PathVariable Long bookingId,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingByIdForOwnerOrBooker(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findBookingsByBookerAndState(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingsByBookerAndState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsByOwnerAndState(@RequestParam(required = false, defaultValue = "ALL") BookingState state,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingsByOwnerAndState(userId, state);
    }

}
