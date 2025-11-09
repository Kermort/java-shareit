package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForRequestInfoDto {
    private Long id;
    private String name;
    private Long ownerId;
    private Long requestId;
}
