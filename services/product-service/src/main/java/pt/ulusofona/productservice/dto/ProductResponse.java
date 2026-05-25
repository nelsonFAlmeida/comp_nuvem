package pt.ulusofona.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for Product responses.
 * 
 * <p>This class is used to send product data in HTTP responses. It contains
 * all relevant product information including timestamps. The DTO pattern
 * allows us to control exactly what data is exposed to clients, hiding
 * internal implementation details.
 * 
 * <p>This DTO includes:
 * <ul>
 *   <li>id - Product's unique identifier</li>
 *   <li>name - Product name</li>
 *   <li>description - Product description</li>
 *   <li>price - Product price</li>
 *   <li>stockQuantity - Current stock quantity</li>
 *   <li>createdAt - Timestamp when product was created</li>
 *   <li>updatedAt - Timestamp when product was last updated</li>
 * </ul>
 * 
 * <p>The response is automatically serialized to JSON by Spring when
 * returned from a REST controller.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see ProductRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    /**
     * Product's unique identifier.
     * Primary key from the database.
     */
    private Long id;
    
    /**
     * Product name.
     */
    private String name;
    
    /**
     * Product description.
     * Can contain up to 1000 characters.
     */
    private String description;
    
    /**
     * Product price.
     * Stored as BigDecimal for precise decimal representation.
     */
    private BigDecimal price;
    
    /**
     * Current stock quantity.
     * Represents the number of units available in inventory.
     */
    private Integer stockQuantity;
    
    /**
     * Timestamp indicating when the product was created.
     * Automatically set when the product is first persisted.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp indicating when the product was last updated.
     * Automatically updated whenever the product entity is modified.
     */
    private LocalDateTime updatedAt;
}
