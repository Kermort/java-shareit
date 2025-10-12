package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemForRequestInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestDtoMapper {
    public static ItemRequest toModel(ItemRequestDescriptionOnlyDto dto, User requestor, LocalDateTime created) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(created)
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorName(itemRequest.getRequestor().getName())
                .build();
    }

    public static ItemRequestWithItemsDto toInfoWithItemsDto(ItemRequest itemRequest, List<ItemForRequestInfoDto> items) {
        return ItemRequestWithItemsDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//      private Long id;
//    @Column(name = "description")
//      private String description;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//      private User requestor;
//    @Column(name = "created")
//      private LocalDateTime created;
