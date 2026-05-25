package pt.ulusofona.productservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a Product in the system.
 * 
 * <p>This class maps to the "products" table in the database and represents
 * a product entity with the following attributes:
 * <ul>
 *   <li>id - Primary key, auto-generated</li>
 *   <li>name - Product name (required, cannot be blank)</li>
 *   <li>description - Product description (required, max 1000 characters)</li>
 *   <li>price - Product price (required, must be greater than zero)</li>
 *   <li>stockQuantity - Current stock quantity (defaults to 0 if not provided)</li>
 *   <li>createdAt - Timestamp when the product was created</li>
 *   <li>updatedAt - Timestamp when the product was last updated</li>
 * </ul>
 * 
 * <p>The class uses JPA annotations for persistence and Jakarta Validation
 * annotations for input validation. Lombok annotations are used to reduce
 * boilerplate code (getters, setters, constructors).
 * 
 * <p>Lifecycle callbacks (@PrePersist and @PreUpdate) automatically set
 * timestamps when entities are created or updated, and initialize stockQuantity
 * to 0 if not provided.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see jakarta.persistence.Entity
 * @see jakarta.validation.constraints.DecimalMin
 * @see jakarta.validation.constraints.NotBlank
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Primary key identifier for the product.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Product name.
     * Cannot be null or blank. Validated using Jakarta Validation.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String name;

    /**
     * Product description.
     * Cannot be null or blank. Maximum length is 1000 characters.
     */
    @NotBlank(message = "Descrição é obrigatória")
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Product price.
     * Must be greater than zero. Stored with precision 10 and scale 2
     * (e.g., 99999999.99).
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Current stock quantity.
     * Defaults to 0 if not provided when creating a new product.
     * Can be null, but will be set to 0 by @PrePersist callback.
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    /**
     * Timestamp indicating when the product was created.
     * Automatically set by @PrePersist lifecycle callback.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the product was last updated.
     * Automatically updated by @PreUpdate lifecycle callback.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback method executed before persisting a new entity.
     * 
     * <p>This method is automatically called by JPA before a new Product entity
     * is persisted to the database. It:
     * <ul>
     *   <li>Sets both createdAt and updatedAt timestamps to the current date and time</li>
     *   <li>Initializes stockQuantity to 0 if it is null</li>
     * </ul>
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stockQuantity == null) {
            stockQuantity = 0;
        }
    }

    /**
     * Lifecycle callback method executed before updating an existing entity.
     * 
     * <p>This method is automatically called by JPA before an existing Product entity
     * is updated in the database. It updates the updatedAt timestamp to the
     * current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
