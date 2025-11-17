package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void findAll_whenInvoked_thenReturnUserDtoList() {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("email@email")
                .build();
        List<UserDto> expectedUsers = List.of(userDto);
        when(userService.findAll()).thenReturn(expectedUsers);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expectedUsers), result);
        verify(userService).findAll();
    }

    @SneakyThrows
    @Test
    void findById_whenInvoked_thenReturnUserDto() {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("email@email")
                .build();
        when(userService.findById(userId)).thenReturn(userDto);

        String result = mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).findById(userId);
    }

    @SneakyThrows
    @Test
    void create_whenInvoked_thenReturnUserDto() {
        UserDto newUserDto = UserDto.builder()
                .name("name")
                .email("email@email")
                .build();
        UserDto createdUserDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email")
                .build();
        when(userService.create(newUserDto)).thenReturn(createdUserDto);

        String result = mockMvc.perform(post("/users")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(createdUserDto), result);
    }

    @SneakyThrows
    @Test
    void patch_whenSuccess_thenStatusIsOkAndReturnUSerDto() {
        Long userId = 1L;
        UserDto patchedUserDto = UserDto.builder()
                .id(userId)
                .name("name")
                .email("email@email")
                .build();
        when(userService.patch(userId, patchedUserDto)).thenReturn(patchedUserDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patchedUserDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(patchedUserDto), result);
        verify(userService).patch(userId, patchedUserDto);
    }

    @SneakyThrows
    @Test
    void delete_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                        .andExpect(status().isOk());

        verify(userService).delete(userId);
    }
}
