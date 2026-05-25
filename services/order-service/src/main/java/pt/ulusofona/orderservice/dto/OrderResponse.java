package pt.ulusofona.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.ulusofona.orderservice.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for Order responses.
 * 
 * <p>This class is used to send order data in HTTP responses. It contains
 * all relevant order information including items and timestamps.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    /**
     * Order's unique identifier.
     */
    private Long id;
    
    /**
     * ID of the user who placed the order.
     */
    private Long userId;
    
    /**
     * List of items in the order.
     */
    private List<OrderItemResponse> items;
    
    /**
     * Total amount of the order.
     */
    private BigDecimal totalAmount;
    
    /**
     * Current status of the order.
     */
    private OrderStatus status;
    
    /**
     * Timestamp when the order was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the order was last updated.
     */
    private LocalDateTime updatedAt;
}

