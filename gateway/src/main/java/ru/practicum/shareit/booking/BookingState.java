package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    WAITING,
    APPROVED,
    CANCELLED,
    REJECTED,
    PAST,
    CURRENT,
    FUTURE,
    ALL;

    public static Optional<BookingState> from(String stateParam) {
        try {
            return Optional.of(BookingState.valueOf(stateParam));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
