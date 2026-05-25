package pt.ulusofona.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulusofona.orderservice.model.OrderStatus;

import java.time.LocalDateTime;

/**
 * Kafka event published when an order status changes.
 * 
 * <p>This event is published to the "order-status-changed" Kafka topic whenever
 * an order's status is updated. Other services can subscribe to this topic to
 * react to status changes.
 * 
 * <p>Event consumers might:
 * <ul>
 *   <li>Send status update emails to customers</li>
 *   <li>Update inventory when order is confirmed</li>
 *   <li>Trigger shipping notifications when order is shipped</li>
 *   <li>Update analytics dashboards</li>
 * </ul>
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderCreatedEvent
 * @see OrderStatus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusChangedEvent {
    
    /**
     * Unique identifier of the order.
     */
    private Long orderId;
    
    /**
     * ID of the user who placed the order.
     */
    private Long userId;
    
    /**
     * Previous status of the order.
     */
    private OrderStatus previousStatus;
    
    /**
     * New status of the order.
     */
    private OrderStatus newStatus;
    
    /**
     * Timestamp when the status change occurred.
     */
    private LocalDateTime changedAt;
}

