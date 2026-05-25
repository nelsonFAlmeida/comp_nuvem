package pt.ulusofona.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents an order item in a Kafka event.
 * 
 * <p>This class is used within OrderCreatedEvent and OrderStatusChangedEvent
 * to represent individual items in an order.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderCreatedEvent
 * @see OrderStatusChangedEvent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    
    /**
     * ID of the product.
     */
    private Long productId;
    
    /**
     * Name of the product.
     */
    private String productName;
    
    /**
     * Quantity ordered.
     */
    private Integer quantity;
    
    /**
     * Price per unit.
     */
    private BigDecimal price;
}

