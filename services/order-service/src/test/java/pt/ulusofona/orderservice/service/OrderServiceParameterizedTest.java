package pt.ulusofona.orderservice.service;

import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.model.Order;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.repository.OrderRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceParameterizedTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private pt.ulusofona.orderservice.client.UserServiceClient userServiceClient;

    @Mock
    private pt.ulusofona.orderservice.client.ProductServiceClient productServiceClient;

    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    @ParameterizedTest(name = "getOrderById id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getOrderById_WhenExists_ReturnsOrder(long id) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.TEN);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.getOrderById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(orderRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "getOrderById id={0} throws")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107, 108})
    void getOrderById_WhenNotExists_Throws(long id) {
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(id));

        verify(orderRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "getOrdersByUserId userId={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getOrdersByUserId_ReturnsList(long userId) {
        when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getOrdersByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByUserId(userId);
    }

    @ParameterizedTest(name = "getAllOrders returns size {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void getAllOrders_ReturnsCorrectSize(int count) {
        List<Order> orders = new java.util.ArrayList<>();
        for (long i = 0; i < count; i++) {
            Order o = new Order();
            o.setId(i + 1);
            o.setUserId(1L);
            o.setStatus(OrderStatus.PENDING);
            o.setTotalAmount(BigDecimal.ONE);
            o.setCreatedAt(LocalDateTime.now());
            o.setUpdatedAt(LocalDateTime.now());
            orders.add(o);
        }
        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderResponse> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(count, result.size());
        verify(orderRepository, times(1)).findAll();
    }
}
