package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Добавление нового запроса на бронирование
     */
    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingDto, Long userId) {
        log.info("создание бронирования вещи с id {} пользователем с id {}", bookingDto.getItemId(), userId);
        if (userId == null || userId <= 0) {
            throw new ValidationException("Ошибка в id пользователя при создании бронирования");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с id \" + userId + \"не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("вешь с id \" + id + \" не найдена."));
        if (!item.getAvailable()) {
            throw new ValidationException("вещь с id " + item.getId() + " не доступна для бронирования");
        }
        Booking booking = BookingDtoMapper.toModel(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return BookingDtoMapper.toDto(bookingRepository.save(booking), ItemDtoMapper.toDto(item), UserDtoMapper.toDto(user));
    }


    /**
     * Подтверждение или отклонение запроса на бронирование
     */
    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approve) {
        log.info("подтверждение бронирования с id {} устанавливается на {}", bookingId, approve);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("бронирование с id " + bookingId + " не существует"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAuthorizedException("пользователь с id " + userId + " не является владельцем");
        }

        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking result = bookingRepository.save(booking);
        return BookingDtoMapper.toDto(result, ItemDtoMapper.toDto(result.getItem()), UserDtoMapper.toDto((result.getBooker())));
    }

    /**
     * Получение данных о конкретном бронировании автором бронирования или владельцем вещи
     */
    @Override
    public BookingDto findBookingByIdForOwnerOrBooker(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("бронирование с id " + bookingId + " не найдено"));
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new NotAuthorizedException("пользователь с id " + userId + " не является владельцем или заказчиком");
        }

        return BookingDtoMapper.toDto(booking, ItemDtoMapper.toDto(booking.getItem()), UserDtoMapper.toDto((booking.getBooker())));
    }

    /**
     * Получение списка всех бронирований текущего пользователя
     */
    @Override
    public List<BookingDto> findBookingsByBookerAndState(Long bookerId, BookingState bookingState) {
        if (bookerId == null) {
            throw new ValidationException("не указан id пользователя");
        }
        log.info("запрос на получение бронирований пользователя с id {}", bookerId);
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("пользователь с id " + bookerId + " не найден"));

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findAllByBookerId(bookerId);
            case WAITING, REJECTED -> bookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.valueOf(bookingState.name()));
            case CURRENT -> bookings = bookingRepository.findCurrentBookingsByBookerId(bookerId, now);
            case PAST -> bookings = bookingRepository.findPastBookingsByBookerId(bookerId, now);
            case FUTURE -> bookings = bookingRepository.findFutureBookingsByBookerId(bookerId, now);
        }

        return bookings.stream()
                .map(b -> BookingDtoMapper.toDto(b, ItemDtoMapper.toDto(b.getItem()), UserDtoMapper.toDto(b.getBooker())))
                .collect(Collectors.toList());
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя
     */
    @Override
    public List<BookingDto> findBookingsByOwnerAndState(Long ownerId, BookingState bookingState) {
        log.info("запрос на получение бронирований своих вещей пользователя с id {}", ownerId);
        if (ownerId == null) {
            throw new ValidationException("id пользователя пустой");
        }
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("пользователь с id " + ownerId + " не найден"));
        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new NotFoundException("у пользователя нет вещей");
        }

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case ALL -> bookings = bookingRepository.findAllByOwnerId(ownerId);
            case WAITING, REJECTED -> bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(bookingState.name()));
            case CURRENT -> bookings = bookingRepository.findCurrentBookingsByOwnerId(ownerId, now);
            case PAST -> bookings = bookingRepository.findPastBookingsByOwnerId(ownerId, now);
            case FUTURE -> bookings = bookingRepository.findFutureBookingsByOwnerId(ownerId, now);
        }

        return bookings.stream()
                .map(b -> BookingDtoMapper.toDto(b, ItemDtoMapper.toDto(b.getItem()), UserDtoMapper.toDto(b.getBooker())))
                .collect(Collectors.toList());
    }
}
