package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionOnlyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void create_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        ItemRequestDescriptionOnlyDto irDto = new ItemRequestDescriptionOnlyDto();
        irDto.setDescription("description");
        ItemRequestDto resultDto = ItemRequestDto.builder().build();
        when(itemRequestService.create(any(ItemRequestDescriptionOnlyDto.class), eq(userId))).thenReturn(resultDto);

        String result = mockMvc.perform(post("/requests")
                .header("X-Sharer-User-Id", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(irDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(resultDto), result);
        verify(itemRequestService).create(any(ItemRequestDescriptionOnlyDto.class), eq(userId));
    }

    @SneakyThrows
    @Test
    void create_whenUserIdIsNull_thenStatusBadRequest() {
        ItemRequestDescriptionOnlyDto irDto = new ItemRequestDescriptionOnlyDto();
        irDto.setDescription("description");

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(irDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(ItemRequestDescriptionOnlyDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void findByRequestorId_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        List<ItemRequestWithItemsDto> expected = List.of(ItemRequestWithItemsDto.builder().build());
        when(itemRequestService.findByRequestorId(userId)).thenReturn(expected);

        String result = mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", userId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(itemRequestService).findByRequestorId(userId);
    }

    @SneakyThrows
    @Test
    void findByRequestorId_whenIdIsNull_thenStatusIsBadRequest() {
        mockMvc.perform(get("/requests"))
                        .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findByRequestorId(anyLong());
    }

    @SneakyThrows
    @Test
    void findAllByOtherUsers_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        List<ItemRequestDto> expected = List.of(ItemRequestDto.builder().build());
        when(itemRequestService.findAllByOtherUsers(userId)).thenReturn(expected);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(itemRequestService).findAllByOtherUsers(userId);
    }

    @SneakyThrows
    @Test
    void findAllByOtherUsers_whenIdIsNull_thenStatusIsBadRequest() {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).findAllByOtherUsers(anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenSuccess_thenStatusIsOk() {
        Long requestId = 1L;
        ItemRequestWithItemsDto expected = ItemRequestWithItemsDto.builder().build();
        when(itemRequestService.findById(requestId)).thenReturn(expected);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(itemRequestService).findById(requestId);
    }

}
