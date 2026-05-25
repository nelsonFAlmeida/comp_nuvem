package pt.ulusofona.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ulusofona.orderservice.dto.OrderItemRequest;
import pt.ulusofona.orderservice.dto.OrderRequest;
import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController.
 * 
 * <p>This test class verifies the REST API endpoints of the OrderController,
 * including request/response handling, HTTP status codes, and JSON serialization.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@WebMvcTest(controllers = OrderController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder_Success() throws Exception {
        // Given
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        OrderRequest orderRequest = new OrderRequest(1L, Arrays.asList(itemRequest));

        OrderResponse orderResponse = new OrderResponse(
                1L,
                1L,
                Arrays.asList(),
                new BigDecimal("1999.98"),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateOrder_ValidationError() throws Exception {
        // Given - Invalid request (missing userId)
        OrderRequest invalidRequest = new OrderRequest(null, Arrays.asList());

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateOrder_WhenServiceThrows_ShouldReturn400WithMessage() throws Exception {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        OrderRequest orderRequest = new OrderRequest(1L, Arrays.asList(itemRequest));

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new RuntimeException("User not found with ID: 1"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found with ID: 1"))
                .andExpect(jsonPath("$.status").value("400"));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void testGetAllOrders() throws Exception {
        // Given
        OrderResponse order1 = new OrderResponse(
                1L, 1L, Arrays.asList(), new BigDecimal("100.00"),
                OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        OrderResponse order2 = new OrderResponse(
                2L, 2L, Arrays.asList(), new BigDecimal("200.00"),
                OrderStatus.CONFIRMED, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        // When & Then
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void testGetOrderById_Success() throws Exception {
        // Given
        OrderResponse orderResponse = new OrderResponse(
                1L,
                1L,
                Arrays.asList(),
                new BigDecimal("1999.98"),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        // When & Then
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        // Given
        OrderResponse orderResponse = new OrderResponse(
                1L,
                1L,
                Arrays.asList(),
                new BigDecimal("1999.98"),
                OrderStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.getOrdersByUserId(1L)).thenReturn(Arrays.asList(orderResponse));

        // When & Then
        mockMvc.perform(get("/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1L));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        // Given
        OrderResponse orderResponse = new OrderResponse(
                1L,
                1L,
                Arrays.asList(),
                new BigDecimal("1999.98"),
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)).thenReturn(orderResponse);

        // When & Then
        mockMvc.perform(put("/orders/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testGetOrderById_WhenOrderNotFound_ShouldReturn400WithMessage() throws Exception {
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("Order not found with ID: 999"));

        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Order not found with ID: 999"))
                .andExpect(jsonPath("$.status").value("400"));

        verify(orderService, times(1)).getOrderById(999L);
    }
}

