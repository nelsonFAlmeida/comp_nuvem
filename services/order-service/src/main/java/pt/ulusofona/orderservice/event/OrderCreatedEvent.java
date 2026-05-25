package pt.ulusofona.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kafka event published when a new order is created.
 * 
 * <p>This event is published to the "order-created" Kafka topic whenever
 * a new order is successfully created. Other services can subscribe to
 * this topic to react to order creation events.
 * 
 * <p>Event consumers might:
 * <ul>
 *   <li>Send confirmation emails to customers</li>
 *   <li>Update inventory in Product Service</li>
 *   <li>Trigger payment processing</li>
 *   <li>Log order creation for analytics</li>
 * </ul>
 * 
 * <p>The event contains all relevant order information so consumers don't
 * need to make additional API calls to fetch order details.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderStatusChangedEvent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    
    /**
     * Unique identifier of the order.
     */
    private Long orderId;
    
    /**
     * ID of the user who placed the order.
     */
    private Long userId;
    
    /**
     * List of items in the order.
     */
    private List<OrderItemEvent> items;
    
    /**
     * Total amount of the order.
     */
    private BigDecimal totalAmount;
    
    /**
     * Timestamp when the order was created.
     */
    private LocalDateTime createdAt;
}

