package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemInfoDto;

import java.util.List;

/**
 * dto для просмотра информации о вещи
 */
@Data
@Builder
public class ItemFullDataDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingForItemInfoDto lastBooking;
    private BookingForItemInfoDto nextBooking;
    private List<CommentDto> comments;
}
