package pt.ulusofona.orderservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import pt.ulusofona.orderservice.client.ProductResponse;
import pt.ulusofona.orderservice.client.ProductServiceClient;
import pt.ulusofona.orderservice.client.UserResponse;
import pt.ulusofona.orderservice.client.UserServiceClient;
import pt.ulusofona.orderservice.dto.OrderItemRequest;
import pt.ulusofona.orderservice.dto.OrderRequest;
import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.model.Order;
import pt.ulusofona.orderservice.model.OrderItem;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 * 
 * <p>This test class verifies the business logic of the OrderService,
 * including order creation, retrieval, and status updates. It uses
 * Mockito to mock dependencies (repository, Feign clients, Kafka).
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private UserResponse testUser;
    private ProductResponse testProduct;
    private OrderRequest orderRequest;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new UserResponse(
                1L,
                "John Doe",
                "john@example.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Setup test product
        testProduct = new ProductResponse(
                1L,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                10,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Setup order request
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        orderRequest = new OrderRequest(1L, Arrays.asList(itemRequest));

        // Setup saved order
        savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(1L);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setTotalAmount(new BigDecimal("1999.98"));
        savedOrder.setCreatedAt(LocalDateTime.now());
        savedOrder.setUpdatedAt(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        orderItem.setProductName("Laptop");
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("999.99"));
        orderItem.setOrder(savedOrder);
        savedOrder.addOrderItem(orderItem);
    }

    @Test
    void testCreateOrder_Success() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(productServiceClient.getProductById(1L)).thenReturn(testProduct);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponse response = orderService.createOrder(orderRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("1999.98"), response.getTotalAmount());

        verify(userServiceClient, times(1)).getUserById(1L);
        verify(productServiceClient, times(1)).getProductById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaTemplate, times(1)).send(eq("order-created"), any());
    }

    @Test
    void testCreateOrder_UserNotFound() {
        // Given
        when(userServiceClient.getUserById(1L))
                .thenThrow(new RuntimeException("User not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userServiceClient, times(1)).getUserById(1L);
        verify(productServiceClient, never()).getProductById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        // Given
        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(productServiceClient.getProductById(1L))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(userServiceClient, times(1)).getUserById(1L);
        verify(productServiceClient, times(1)).getProductById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        // Given
        ProductResponse lowStockProduct = new ProductResponse(
                1L,
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                1, // Only 1 in stock
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(productServiceClient.getProductById(1L)).thenReturn(lowStockProduct);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest); // Requesting 2 items
        });

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(userServiceClient, times(1)).getUserById(1L);
        verify(productServiceClient, times(1)).getProductById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetAllOrders() {
        // Given
        when(orderRepository.findAll()).thenReturn(Arrays.asList(savedOrder));

        // When
        List<OrderResponse> responses = orderService.getAllOrders();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        // When
        OrderResponse response = orderService.getOrderById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(1L);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrdersByUserId() {
        // Given
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(savedOrder));

        // When
        List<OrderResponse> responses = orderService.getOrdersByUserId(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getUserId());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        savedOrder.setStatus(OrderStatus.CONFIRMED);

        // When
        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Then
        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaTemplate, times(1)).send(eq("order-status-changed"), any());
    }

    @Test
    void testUpdateOrderStatus_NotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);
        });

        assertTrue(exception.getMessage().contains("Order not found"));
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrder_MultipleItems() {
        // Given
        OrderItemRequest item1 = new OrderItemRequest(1L, 2);
        OrderItemRequest item2 = new OrderItemRequest(2L, 1);
        OrderRequest multiItemRequest = new OrderRequest(1L, Arrays.asList(item1, item2));

        ProductResponse product2 = new ProductResponse(
                2L,
                "Mouse",
                "Wireless mouse",
                new BigDecimal("29.99"),
                5,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userServiceClient.getUserById(1L)).thenReturn(testUser);
        when(productServiceClient.getProductById(1L)).thenReturn(testProduct);
        when(productServiceClient.getProductById(2L)).thenReturn(product2);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponse response = orderService.createOrder(multiItemRequest);

        // Then
        assertNotNull(response);
        verify(userServiceClient, times(1)).getUserById(1L);
        verify(productServiceClient, times(1)).getProductById(1L);
        verify(productServiceClient, times(1)).getProductById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}

