package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * dto для запроса о создании комментария
 */
@Data
public class CommentTextOnlyDto {
    @NotBlank
    private String text;
}
