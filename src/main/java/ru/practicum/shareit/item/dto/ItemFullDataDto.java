package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
}
