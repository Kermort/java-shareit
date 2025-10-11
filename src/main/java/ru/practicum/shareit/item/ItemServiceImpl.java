package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotAuthorizedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    /**
     * получение всех вещей пользователя по его id с бронированиями и комментариями
     */
    @Override
    public List<ItemFullDataDto> findAll(Long userId) {
        log.info("получение всех вещей пользователя с id " + userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        LocalDateTime now = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIdsAndStatus(itemIds, now, BookingStatus.APPROVED);
        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIdsAndStatus(itemIds, now, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findAllByItemIds(itemIds);
        return items.stream()
                .map(item -> ItemDtoMapper.toListViewDto(item,
                        lastBookings.stream()
                                .filter(b -> b.getItem().getId().equals(item.getId()))
                                .max(Comparator.comparing(Booking::getEnd))
                                .map(Booking::getEnd)
                                .stream().findAny().orElse(null),
                        nextBookings.stream()
                                .filter(b -> b.getItem().getId().equals(item.getId()))
                                .min(Comparator.comparing(Booking::getStart))
                                .map(Booking::getStart)
                                .stream().findAny().orElse(null),
                        comments.stream()
                                .filter(c -> c.getItem().getId().equals(item.getId()))
                                .map(CommentDtoMapper::toDto)
                                .toList())
                ).toList();
    }

    /**
     * получение информации о вещи пользователем
     */
    @Override
    public ItemFullDataDto findById(Long itemId, Long userId) {
        log.info("получение вещи с id {}", itemId);
        if (itemId == null) {
            throw new ValidationException("id не может быть пустым");
        }
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("вешь с id " + itemId + " не найдена."));
        List<CommentDto> comments = commentRepository.findAllByItemIds(List.of(item.getId())).stream()
                .map(CommentDtoMapper::toDto).toList();
        if (!item.getOwner().getId().equals(userId)) {
            //не владельцу возвращаем без данных о предыдущем и следующем бронировании
            return ItemDtoMapper.toListViewDto(item, null, null, comments);
        }
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIdsAndStatus(List.of(itemId), now, BookingStatus.APPROVED);
        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIdsAndStatus(List.of(itemId), now, BookingStatus.APPROVED);
        LocalDateTime lastBookingDate = lastBookings.stream().map(Booking::getEnd).max(LocalDateTime::compareTo).orElse(null);
        LocalDateTime nextBookingDate = nextBookings.stream().map(Booking::getStart).min(LocalDateTime::compareTo).orElse(null);
        log.info("вещь с id {} last booking = {} now = {} next booking = {}", item.getId(), lastBookingDate, now, nextBookingDate);
        return ItemDtoMapper.toListViewDto(item, lastBookingDate, nextBookingDate, comments);
    }

    /**
     * добавление вещи
     */
    @Override
    @Transactional
    public ItemDto create(ItemDto newItemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с id " + userId + "не найден"));
        Item newItem = ItemDtoMapper.toModel(newItemDto);
        newItem.setOwner(user);

        return ItemDtoMapper.toDto(itemRepository.save(newItem));
    }

    /**
     * изменение вещи
     */
    @Override
    @Transactional
    public ItemDto patch(Long itemId, ItemDto newItemDto, Long userId) {
        log.info("Попытка частичного обновления вещи с id {}", itemId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("не найдена вещь с id " + itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь с id " + userId + " не найден"));

        if (!user.getId().equals(userId)) {
            throw new NotAuthorizedException("пользователь с id " + userId + " не является владельцем вещи с id " + itemId);
        }
        Item newItem = ItemDtoMapper.toModel(newItemDto);

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        itemRepository.save(oldItem);
        return ItemDtoMapper.toDto(oldItem);
    }

    /**
     * поиск вещи по названию и описанию
     */
    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * создание комментария (отзыва)
     */
    @Override
    @Transactional
    public CommentDto createComment(CommentTextOnlyDto dto, Long itemId, Long userId) {
        log.info("создание комментария пользователя с id {} для вещи с id {}", userId, itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("вещь не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("пользователь не найден"));
        List<Booking> bookings = bookingRepository.findPastBookingsByBookerIdAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ValidationException("пользователь, у которого не завершена аренда вещи, не может оставить к ней комментарий");
        }

        Comment comment = CommentDtoMapper.toModel(dto, item, user, LocalDateTime.now());
        return CommentDtoMapper.toDto(commentRepository.save(comment));
    }
}