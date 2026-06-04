package pt.ulusofona.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
 * Unit tests for OrderEventConsumer (SQS-based).
 *
 * <p>The consumer now receives a raw JSON {@code String} from SQS and
 * deserialises it internally with {@link ObjectMapper}. Tests feed the
 * serialised JSON directly to {@link OrderEventConsumer#handleOrderCreated(String)}
 * so the full deserialisation + business logic path is exercised.
 *
 * @author Cloud Computing Course
 * @version 2.0.0
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private ProductRepository productRepository;

    /**
     * Real ObjectMapper with JavaTimeModule so LocalDateTime fields
     * serialise / deserialise correctly inside the consumer.
     */
    @Spy
    private ObjectMapper objectMapper = buildObjectMapper();

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    private Product testProduct;
    private OrderCreatedEvent orderCreatedEvent;
    private String orderCreatedEventJson;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("High-performance laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        // Setup order event and its JSON representation
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

        orderCreatedEventJson = objectMapper.writeValueAsString(orderCreatedEvent);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void testHandleOrderCreated_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When – pass raw JSON string as SQS would deliver it
        orderEventConsumer.handleOrderCreated(orderCreatedEventJson);

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
        // ProductNotFound causes a RuntimeException inside the loop which is re-thrown.
        assertThrows(RuntimeException.class, () ->
                orderEventConsumer.handleOrderCreated(orderCreatedEventJson));

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testHandleOrderCreated_InsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Only 1 in stock, but ordering 2
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When – consumer logs a warning and continues (no save)
        orderEventConsumer.handleOrderCreated(orderCreatedEventJson);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
        // Stock should not be updated when insufficient
    }

    @Test
    void testHandleOrderCreated_MultipleItems() throws Exception {
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

        String multiItemJson = objectMapper.writeValueAsString(multiItemEvent);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        orderEventConsumer.handleOrderCreated(multiItemJson);

        // Then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
        verify(productRepository, times(2)).save(any(Product.class));
        assertEquals(8, testProduct.getStockQuantity()); // 10 - 2 = 8
        assertEquals(4, product2.getStockQuantity()); // 5 - 1 = 4
    }

    @Test
    void testHandleOrderCreated_InvalidJson_ShouldReturnWithoutThrowing() {
        // Given – simulate a corrupt/unparseable SQS message
        String badJson = "NOT_VALID_JSON";

        // When & Then – consumer logs the error and returns silently (no throw)
        assertDoesNotThrow(() -> orderEventConsumer.handleOrderCreated(badJson));

        verifyNoInteractions(productRepository);
    }

    @Test
    void testHandleOrderCreated_WhenSaveThrows_ShouldRethrow() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));

        // When & Then – re-thrown so SQS can redeliver the message
        assertThrows(RuntimeException.class, () ->
                orderEventConsumer.handleOrderCreated(orderCreatedEventJson));

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
