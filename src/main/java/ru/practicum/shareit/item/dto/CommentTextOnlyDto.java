package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * dto для запроса и ответа о создании
 */
@Data
@Builder
public class CommentTextOnlyDto {
    private Long id;
    @NotBlank
    private String text;
}
