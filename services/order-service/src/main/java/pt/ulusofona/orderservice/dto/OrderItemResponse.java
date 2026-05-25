package pt.ulusofona.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for an order item in a response.
 * 
 * <p>This class represents a single product item within an order response.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    
    /**
     * Order item's unique identifier.
     */
    private Long id;
    
    /**
     * ID of the product.
     */
    private Long productId;
    
    /**
     * Name of the product (snapshot at time of order).
     */
    private String productName;
    
    /**
     * Quantity of the product.
     */
    private Integer quantity;
    
    /**
     * Price per unit (snapshot at time of order).
     */
    private BigDecimal price;
}

