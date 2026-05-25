package pt.ulusofona.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pt.ulusofona.orderservice.dto.OrderItemRequest;
import pt.ulusofona.orderservice.dto.OrderRequest;
import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@ContextConfiguration(classes = {OrderController.class, GlobalExceptionHandler.class})
class OrderControllerParameterizedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest(name = "getOrderById id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
    void getOrderById_WithValidId_Returns200(long id) throws Exception {
        OrderResponse order = new OrderResponse(id, 1L, Collections.emptyList(), BigDecimal.TEN, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        when(orderService.getOrderById(id)).thenReturn(order);

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        verify(orderService, times(1)).getOrderById(id);
    }

    @ParameterizedTest(name = "getOrderById not found id={0}")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107, 108})
    void getOrderById_WhenNotFound_Returns400(long id) throws Exception {
        when(orderService.getOrderById(id)).thenThrow(new RuntimeException("Order not found with ID: " + id));

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));

        verify(orderService, times(1)).getOrderById(id);
    }

    @ParameterizedTest(name = "getOrdersByUserId userId={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void getOrdersByUserId_Returns200(long userId) throws Exception {
        when(orderService.getOrdersByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(orderService, times(1)).getOrdersByUserId(userId);
    }
}
