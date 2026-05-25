package pt.ulusofona.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity class representing an Order Item (product in an order).
 * 
 * <p>This class maps to the "order_items" table in the database and represents
 * a single product item within an order. Each order can contain multiple items.
 * 
 * <p>Attributes:
 * <ul>
 *   <li>id - Primary key, auto-generated</li>
 *   <li>order - Reference to the parent order (many-to-one relationship)</li>
 *   <li>productId - ID of the product (references Product Service)</li>
 *   <li>productName - Name of the product (snapshot at time of order)</li>
 *   <li>quantity - Number of units ordered</li>
 *   <li>price - Price per unit at time of order (snapshot)</li>
 * </ul>
 * 
 * <p>The product name and price are stored as snapshots to preserve the order
 * information even if the product details change in the Product Service.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see Order
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Primary key identifier for the order item.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the parent order.
     * Many-to-one relationship with Order entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * ID of the product in this order item.
     * This references a product in the Product Service (external service).
     * Cannot be null.
     */
    @NotNull(message = "Product ID is required")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Name of the product at the time the order was placed.
     * This is a snapshot to preserve historical order data even if
     * the product name changes in the Product Service.
     */
    @Column(name = "product_name", nullable = false)
    private String productName;

    /**
     * Quantity of this product in the order.
     * Must be at least 1.
     */
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price per unit at the time the order was placed.
     * This is a snapshot to preserve historical order data even if
     * the product price changes in the Product Service.
     * Stored with precision 10 and scale 2.
     */
    @NotNull(message = "Price is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}

