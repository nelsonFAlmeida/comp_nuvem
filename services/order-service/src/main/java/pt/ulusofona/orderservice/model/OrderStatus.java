package pt.ulusofona.orderservice.model;

/**
 * Enumeration representing the possible statuses of an order.
 * 
 * <p>This enum defines the lifecycle states that an order can be in:
 * <ul>
 *   <li>PENDING - Order has been created but not yet confirmed</li>
 *   <li>CONFIRMED - Order has been confirmed and is being processed</li>
 *   <li>SHIPPED - Order has been shipped to the customer</li>
 *   <li>DELIVERED - Order has been delivered to the customer</li>
 *   <li>CANCELLED - Order has been cancelled</li>
 * </ul>
 * 
 * <p>The status transitions typically follow this flow:
 * PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
 * 
 * <p>An order can be CANCELLED from any state except DELIVERED.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see Order
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not yet confirmed.
     * This is the initial state when an order is first created.
     */
    PENDING,
    
    /**
     * Order has been confirmed and is being processed.
     * Payment has been verified and the order is ready for fulfillment.
     */
    CONFIRMED,
    
    /**
     * Order has been shipped to the customer.
     * The order is in transit.
     */
    SHIPPED,
    
    /**
     * Order has been delivered to the customer.
     * This is the final state for a successful order.
     */
    DELIVERED,
    
    /**
     * Order has been cancelled.
     * Can be cancelled from any state except DELIVERED.
     */
    CANCELLED
}

