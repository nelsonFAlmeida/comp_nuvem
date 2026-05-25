package pt.ulusofona.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) for creating a new Order.
 * 
 * <p>This class is used to receive order data from HTTP requests. It contains
 * validation annotations to ensure data integrity before processing.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>userId - Cannot be null</li>
 *   <li>items - Cannot be null or empty, must contain at least one item</li>
 *   <li>Each item is validated using @Valid annotation</li>
 * </ul>
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderItemRequest
 * @see OrderResponse
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    /**
     * ID of the user placing the order.
     * This will be validated against the User Service using OpenFeign.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * List of items in the order.
     * Must contain at least one item. Each item is validated.
     */
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;
}

