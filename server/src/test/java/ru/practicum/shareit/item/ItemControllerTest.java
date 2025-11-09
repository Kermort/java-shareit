package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextOnlyDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDataDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @SneakyThrows
    @Test
    void findAll_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        ItemFullDataDto itemFullDataDto = ItemFullDataDto.builder().build();
        List<ItemFullDataDto> expectedItems = List.of(itemFullDataDto);
        when(itemService.findAll(userId)).thenReturn(expectedItems);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertEquals(objectMapper.writeValueAsString(expectedItems), result);
        verify(itemService).findAll(userId);
    }

    @SneakyThrows
    @Test
    void findAll_whenUserIdIsNull_thenStatusIsBadRequest() {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAll(anyLong());
    }

    @SneakyThrows
    @Test
    void findById_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemFullDataDto expectedDto = ItemFullDataDto.builder().build();
        when(itemService.findById(itemId, userId)).thenReturn(expectedDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expectedDto), result);
        verify(itemService).findById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void findById_whenUserIdIsNull_thenStatusIsBadRequest() {
        Long itemId = 1L;
        mockMvc.perform(get("/items/{itemId}", itemId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAll(anyLong());
    }

    @SneakyThrows
    @Test
    void patch_whenSuccess_thenStatusIsOk() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        when(itemService.patch(itemId, itemDto, userId)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).patch(itemId, itemDto, userId);
    }

    @SneakyThrows
    @Test
    void patch_whenUserIdIsNull_thenStatusIsBadRequest() {
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).patch(anyLong(), any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void search_whenSuccess_thenStatusIsOk() {
        String text = "text";
        ItemDto itemDto = ItemDto.builder().build();
        List<ItemDto> expected = List.of(itemDto);
        when(itemService.search(text)).thenReturn(expected);

        String result = mockMvc.perform(get("/items/search")
                .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(itemService).search(text);
    }

    @SneakyThrows
    @Test
    void createItem_whenSuccess_thenStatusIsOk() {
        ItemDto newItemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        Long userId = 1L;
        when(itemService.create(newItemDto, userId)).thenReturn(newItemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(newItemDto), result);
        verify(itemService).create(newItemDto, userId);
    }

    @SneakyThrows
    @Test
    void createItem_whenItemIsInvalid_thenStatusIsBadRequest() {
        Long userId = 1L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createItem_whenUserIdIsNull_thenStatusIsBadRequest() {
        ItemDto newItemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(any(ItemDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentTextOnlyDto comment = new CommentTextOnlyDto();
        CommentDto expected = CommentDto.builder().text("text").build();
        comment.setText("text");
        Long itemId = 1L;
        Long userId = 1L;
        when(itemService.createComment(comment, itemId, userId)).thenReturn(expected);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(itemService).createComment(comment, itemId, userId);
    }
}
