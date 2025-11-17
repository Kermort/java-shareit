package ru.practicum.shareit.request;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;
    private final JacksonTester<ItemRequestWithItemsDto> json2;

    @SneakyThrows
    @Test
    void testSerializeItemRequestDto() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2025, 9, 13, 0, 0))
                .requestorName("Name")
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.requestorName");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-09-13T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.requestorName").isEqualTo(dto.getRequestorName());
    }

    @SneakyThrows
    @Test
    void testDeserializeItemRequestDto() {
        String content = "{\"id\": \"1\"," +
                         "\"description\": \"description\"," +
                         "\"created\": \"2025-09-13T00:00:00\"," +
                         "\"requestorName\": \"Name\"" +
                         "}";

        ItemRequestDto result = json.parse(content).getObject();
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("description", "description");
        assertThat(result).hasFieldOrPropertyWithValue("created", LocalDateTime.of(2025, 9, 13, 0, 0));
        assertThat(result).hasFieldOrPropertyWithValue("requestorName", "Name");
    }

    @SneakyThrows
    @Test
    void testSerializeItemRequestWithItemsDto() {
        ItemForRequestInfoDto itemDto = ItemForRequestInfoDto.builder()
                .id(1L)
                .name("name")
                .ownerId(10L)
                .requestId(20L)
                .build();
        ItemRequestWithItemsDto irDto = ItemRequestWithItemsDto.builder()
                .id(30L)
                .description("description")
                .created(LocalDateTime.of(2025, 9, 13, 0, 0))
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestWithItemsDto> result = json2.write(irDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items[0].id");
        assertThat(result).hasJsonPath("$.items[0].name");
        assertThat(result).hasJsonPath("$.items[0].ownerId");
        assertThat(result).hasJsonPath("$.items[0].requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(irDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(irDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-09-13T00:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(itemDto.getOwnerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(itemDto.getRequestId().intValue());
    }

    @SneakyThrows
    @Test
    void testDeserializeItemRequestWithItemsDto() {
        String content = "{\"id\": \"1\"," +
                         "\"description\": \"description\"," +
                         "\"created\": \"2025-09-13T00:00:00\"," +
                         "\"items\": [{" +
                         "\"id\": \"1\"," +
                         "\"name\": \"name\"," +
                         "\"ownerId\": \"10\"," +
                         "\"requestId\": \"20\"" +
                         "}]}";

        ItemRequestWithItemsDto result = json2.parse(content).getObject();
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("description", "description");
        assertThat(result).hasFieldOrPropertyWithValue("created", LocalDateTime.of(2025, 9, 13, 0, 0));
        assertThat(result.getItems().get(0)).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result.getItems().get(0)).hasFieldOrPropertyWithValue("name", "name");
        assertThat(result.getItems().get(0)).hasFieldOrPropertyWithValue("ownerId", 10L);
        assertThat(result.getItems().get(0)).hasFieldOrPropertyWithValue("requestId", 20L);
    }
}
