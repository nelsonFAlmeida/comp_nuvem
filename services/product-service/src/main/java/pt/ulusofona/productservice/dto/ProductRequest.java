package pt.ulusofona.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for creating or updating a Product.
 * 
 * <p>This class is used to receive product data from HTTP requests. It contains
 * validation annotations to ensure data integrity before processing. The DTO
 * pattern separates the API contract from the internal entity model, providing
 * better control over what data is exposed and validated.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>name - Cannot be null or blank</li>
 *   <li>description - Cannot be null or blank</li>
 *   <li>price - Must be greater than zero</li>
 *   <li>stockQuantity - Optional, defaults to 0 if not provided</li>
 * </ul>
 * 
 * <p>Invalid requests are automatically handled by the GlobalExceptionHandler,
 * which returns appropriate error messages to the client.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see ProductResponse
 * @see jakarta.validation.constraints.NotBlank
 * @see jakarta.validation.constraints.DecimalMin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    /**
     * Product name.
     * 
     * <p>This field is required and cannot be null or blank. The validation
     * is performed automatically by Spring when the @Valid annotation is
     * used in the controller.
     */
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    /**
     * Product description.
     * 
     * <p>This field is required and cannot be null or blank. It can contain
     * up to 1000 characters (enforced at the entity level).
     */
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    /**
     * Product price.
     * 
     * <p>This field is required and must be greater than zero. It uses
     * BigDecimal for precise decimal representation, avoiding floating-point
     * precision issues common with float or double types.
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    private BigDecimal price;

    /**
     * Current stock quantity.
     * 
     * <p>This field is optional. If not provided, it will default to 0
     * when the product is created (handled by the entity's @PrePersist callback).
     */
    private Integer stockQuantity;
}
