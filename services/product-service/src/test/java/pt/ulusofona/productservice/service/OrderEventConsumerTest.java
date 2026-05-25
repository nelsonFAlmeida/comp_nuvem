package pt.ulusofona.productservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ulusofona.productservice.event.OrderCreatedEvent;
import pt.ulusofona.productservice.event.OrderItemEvent;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderEventConsumer.
 * 
 * <p>This test class verifies that the Kafka event consumer correctly
 * processes OrderCreatedEvent and updates product inventory.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    private Product testProduct;
    private OrderCreatedEvent orderCreatedEvent;

    @BeforeEach
    void setUp() {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("High-performance laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        // Setup order event
        OrderItemEvent itemEvent = new OrderItemEvent(
                1L,
                "Laptop",
                2,
                new BigDecimal("999.99")
        );

        orderCreatedEvent = new OrderCreatedEvent(
                1L,
                1L,
                Arrays.asList(itemEvent),
                new BigDecimal("1999.98"),
                LocalDateTime.now()
        );
    }

    @Test
    void testHandleOrderCreated_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        orderEventConsumer.handleOrderCreated(orderCreatedEvent);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        assertEquals(8, testProduct.getStockQuantity()); // 10 - 2 = 8
    }

    @Test
    void testHandleOrderCreated_ProductNotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        orderEventConsumer.handleOrderCreated(orderCreatedEvent);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testHandleOrderCreated_InsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Only 1 in stock, but ordering 2
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        orderEventConsumer.handleOrderCreated(orderCreatedEvent);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
        // Stock should not be updated when insufficient
    }

    @Test
    void testHandleOrderCreated_MultipleItems() {
        // Given
        OrderItemEvent item1 = new OrderItemEvent(1L, "Laptop", 2, new BigDecimal("999.99"));
        OrderItemEvent item2 = new OrderItemEvent(2L, "Mouse", 1, new BigDecimal("29.99"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setStockQuantity(5);

        OrderCreatedEvent multiItemEvent = new OrderCreatedEvent(
                1L,
                1L,
                Arrays.asList(item1, item2),
                new BigDecimal("2029.97"),
                LocalDateTime.now()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        orderEventConsumer.handleOrderCreated(multiItemEvent);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
        verify(productRepository, times(2)).save(any(Product.class));
        assertEquals(8, testProduct.getStockQuantity()); // 10 - 2 = 8
        assertEquals(4, product2.getStockQuantity()); // 5 - 1 = 4
    }

    @Test
    void testHandleOrderCreated_WhenSaveThrows_ShouldContinueWithoutRethrowing() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));

        assertDoesNotThrow(() -> orderEventConsumer.handleOrderCreated(orderCreatedEvent));

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}

