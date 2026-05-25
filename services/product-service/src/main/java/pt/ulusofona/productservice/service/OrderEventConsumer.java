package pt.ulusofona.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.productservice.event.OrderCreatedEvent;
import pt.ulusofona.productservice.event.OrderItemEvent;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;

/**
 * Kafka event consumer for order-related events.
 * 
 * <p>This service consumes events from Kafka topics published by the Order Service.
 * It handles:
 * <ul>
 *   <li>OrderCreatedEvent - Updates product inventory when orders are created</li>
 * </ul>
 * 
 * <p>This demonstrates asynchronous, event-driven communication between microservices.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderCreatedEvent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ProductRepository productRepository;

    /**
     * Consumes OrderCreatedEvent from Kafka.
     * 
     * <p>This method is automatically invoked when a message is received on the
     * "order-created" topic. It updates the stock quantity for each product
     * in the order by subtracting the ordered quantity.
     * 
     * <p>Note: In a production system, you might want to implement idempotency
     * checks to handle duplicate events.
     * 
     * @param event The OrderCreatedEvent received from Kafka
     * @apiNote This method uses a write transaction
     */
    @KafkaListener(topics = "order-created", groupId = "product-service-group")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order ID: {}", event.getOrderId());

        try {
            for (OrderItemEvent item : event.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException(
                                "Product not found with ID: " + item.getProductId()));

                int newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) {
                    log.warn("Insufficient stock for product {} (Order ID: {}). Current: {}, Requested: {}",
                            product.getName(), event.getOrderId(), product.getStockQuantity(), item.getQuantity());
                    // In production, you might want to publish a compensation event
                    continue;
                }

                product.setStockQuantity(newStock);
                productRepository.save(product);
                log.info("Updated stock for product {}: {} -> {} (Order ID: {})",
                        product.getName(), product.getStockQuantity() + item.getQuantity(),
                        newStock, event.getOrderId());
            }
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order ID: {}", event.getOrderId(), e);
            // In production, you might want to send the event to a dead letter queue
        }
    }
}

