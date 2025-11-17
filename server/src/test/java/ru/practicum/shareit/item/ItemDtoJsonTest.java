package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @SneakyThrows
    @Test
    void testSerializeItemDto() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        JsonContent<ItemDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(dto.getAvailable());
        assertThat(result).extractingJsonPathValue("$.requestID").isEqualTo(null);
    }

    @SneakyThrows
    @Test
    void testDeserializeItemDto() {
        String content = "{\"id\": \"1\",\n" +
                         "\"name\": \"name\",\n" +
                         "\"description\": \"description\",\n" +
                         "\"available\": \"true\"\n" +
                         "}";

        ItemDto result = json.parse(content).getObject();
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("name", "name");
        assertThat(result).hasFieldOrPropertyWithValue("description", "description");
        assertThat(result).hasFieldOrPropertyWithValue("available", true);
        assertThat(result).hasFieldOrPropertyWithValue("requestId", null);
    }
}
