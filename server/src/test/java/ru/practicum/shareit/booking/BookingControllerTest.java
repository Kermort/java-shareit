package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void create_whenSuccess_thenStatusIsOk() {
        Long itemId = 1L;
        Long userId = 1L;
        BookingRequestDto newBookingDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto expected = BookingDto.builder().build();
        when(bookingService.create(newBookingDto, userId)).thenReturn(expected);

        String result = mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(bookingService).create(newBookingDto, userId);
    }

    @SneakyThrows
    @Test
    void create_whenBookingIsNotValid_thenStatusIsBadRequest() {
        Long userId = 1L;
        BookingRequestDto newBookingDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingRequestDto.class), anyLong());
    }

    @SneakyThrows
    @Test
    void approveBooking_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingDto expected = BookingDto.builder().build();
        when(bookingService.approveBooking(bookingId, userId, true)).thenReturn(expected);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", userId)
                .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(bookingService).approveBooking(bookingId, userId, true);
    }

    @SneakyThrows
    @Test
    void approveBooking_whenUserIdIsNull_thenStatusIsBadRequest() {
        Long bookingId = 1L;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void findBookingsByBookerAndState_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        List<BookingDto> expected = List.of(BookingDto.builder().build());
        when(bookingService.findBookingsByBookerAndState(userId, BookingState.ALL)).thenReturn(expected);

        String result = mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state", "ALL"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(bookingService).findBookingsByBookerAndState(userId, BookingState.ALL);
    }

    @SneakyThrows
    @Test
    void findBookingsByBookerAndState_whenUserIdIsNull_thenStatusIsBadRequest() {
        mockMvc.perform(get("/bookings")
                        .param("state", "ALL"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findBookingsByBookerAndState(anyLong(), any(BookingState.class));
    }

    @SneakyThrows
    @Test
    void findBookingsByOwnerAndState_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        List<BookingDto> expected = List.of(BookingDto.builder().build());
        when(bookingService.findBookingsByOwnerAndState(userId, BookingState.ALL)).thenReturn(expected);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(bookingService).findBookingsByOwnerAndState(userId, BookingState.ALL);
    }

    @SneakyThrows
    @Test
    void findBookingByIdForOwnerOrBooker_whenSuccess_thenStatusIsOk() {
        Long userId = 1L;
        Long bookingId = 1L;
        BookingDto expected = BookingDto.builder().build();
        when(bookingService.findBookingByIdForOwnerOrBooker(bookingId, userId)).thenReturn(expected);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(result);
        assertEquals(objectMapper.writeValueAsString(expected), result);
        verify(bookingService).findBookingByIdForOwnerOrBooker(bookingId, userId);
    }
}
