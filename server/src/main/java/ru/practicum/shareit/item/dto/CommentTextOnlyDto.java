package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * dto для запроса о создании комментария
 */
@Data
public class CommentTextOnlyDto {
    private String text;
}
