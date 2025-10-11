package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class BookingDtoMapper {
    public static BookingDto toDto(Booking booking, ItemDto itemDto, UserDto userDto) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(itemDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(userDto)
                .build();
    }

    public static Booking toModel(BookingDto dto, Item item, User user) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .status(dto.getStatus())
                .item(item)
                .booker(user)
                .build();
    }

    public static Booking toModel(BookingRequestDto dto, Item item, User user) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(user)
                .build();
    }
}
