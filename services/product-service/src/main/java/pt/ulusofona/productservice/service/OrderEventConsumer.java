package pt.ulusofona.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.productservice.event.OrderCreatedEvent;
import pt.ulusofona.productservice.event.OrderItemEvent;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;

/**
 * AWS SQS event consumer for order-related events (replaces Kafka-based consumer).
 *
 * <p>This service polls the SQS queue configured via {@code CLOUD_AWS_SQS_ENDPOINT}
 * and handles:
 * <ul>
 *   <li>{@link OrderCreatedEvent} – updates product inventory when orders are placed</li>
 * </ul>
 *
 * <p>Demonstrates asynchronous, event-driven communication between microservices
 * using AWS SQS as the message broker.
 *
 * @author Cloud Computing Course
 * @version 2.0.0
 * @since 2.0.0
 * @see OrderCreatedEvent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    /**
     * Consumes raw JSON messages from the SQS order-events queue.
     *
     * <p>Each message is deserialised into an {@link OrderCreatedEvent} and the
     * stock quantity for every product in the order is decremented accordingly.
     *
     * <p>The queue URL is resolved at runtime from the {@code CLOUD_AWS_SQS_ENDPOINT}
     * environment variable; the {@code ${…}} placeholder is evaluated by Spring before
     * being passed to the SQS listener infrastructure.
     *
     * <p>Note: In a production system consider implementing idempotency checks to
     * handle duplicate/redelivered messages gracefully.
     *
     * @param rawMessage Raw JSON string received from SQS
     */
    @SqsListener("${CLOUD_AWS_SQS_ENDPOINT}")
    @Transactional
    public void handleOrderCreated(String rawMessage) {
        log.debug("Received raw SQS message: {}", rawMessage);

        OrderCreatedEvent event;
        try {
            event = objectMapper.readValue(rawMessage, OrderCreatedEvent.class);
        } catch (Exception e) {
            log.error("Failed to deserialise SQS message: {}", rawMessage, e);
            // Returning normally acknowledges the message; SQS will not redeliver it.
            // In production use a DLQ to capture unparseable messages.
            return;
        }

        log.info("Processing OrderCreatedEvent for order ID: {}", event.getOrderId());

        try {
            for (OrderItemEvent item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException(
                                "Product not found with ID: " + item.getProductId()));

                int newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) {
                    log.warn("Insufficient stock for product {} (Order ID: {}). Current: {}, Requested: {}",
                            product.getName(), event.getOrderId(),
                            product.getStockQuantity(), item.getQuantity());
                    // In production publish a compensation event
                    continue;
                }

                product.setStockQuantity(newStock);
                productRepository.save(product);
                log.info("Updated stock for product {}: {} -> {} (Order ID: {})",
                        product.getName(),
                        product.getStockQuantity() + item.getQuantity(),
                        newStock,
                        event.getOrderId());
            }
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order ID: {}", event.getOrderId(), e);
            // Re-throw so the message becomes invisible again and is redelivered by SQS.
            throw new RuntimeException("Error processing order event", e);
        }
    }
}
