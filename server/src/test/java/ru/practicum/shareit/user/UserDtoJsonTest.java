package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserDto> json;

    @SneakyThrows
    @Test
    void testSerializeUserDto() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();

        JsonContent<UserDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
    }

    @SneakyThrows
    @Test
    void testDeserializeUserDto() {
        String content = "{\"id\": \"1\"," +
                         "\"name\": \"name\"," +
                         "\"email\": \"email@email.com\"}";

        UserDto result = json.parse(content).getObject();
        assertThat(result).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(result).hasFieldOrPropertyWithValue("name", "name");
        assertThat(result).hasFieldOrPropertyWithValue("email", "email@email.com");
    }
}
