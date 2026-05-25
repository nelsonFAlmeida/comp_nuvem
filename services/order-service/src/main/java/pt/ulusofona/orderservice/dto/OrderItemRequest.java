package pt.ulusofona.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for an order item in a request.
 * 
 * <p>This class represents a single product item to be included in an order.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>productId - Cannot be null</li>
 *   <li>quantity - Must be at least 1</li>
 * </ul>
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    /**
     * ID of the product to order.
     * This will be validated against the Product Service using OpenFeign.
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Quantity of the product to order.
     * Must be at least 1.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required")
    private Integer quantity;
}

