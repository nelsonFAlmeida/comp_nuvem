package pt.ulusofona.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing an Order in the system.
 * 
 * <p>This class maps to the "orders" table in the database and represents
 * an order entity with the following attributes:
 * <ul>
 *   <li>id - Primary key, auto-generated</li>
 *   <li>userId - Reference to the user who placed the order</li>
 *   <li>orderItems - List of items in the order (one-to-many relationship)</li>
 *   <li>totalAmount - Total amount of the order</li>
 *   <li>status - Current status of the order (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)</li>
 *   <li>createdAt - Timestamp when the order was created</li>
 *   <li>updatedAt - Timestamp when the order was last updated</li>
 * </ul>
 * 
 * <p>The class uses JPA annotations for persistence and Jakarta Validation
 * annotations for input validation. Lombok annotations are used to reduce
 * boilerplate code (getters, setters, constructors).
 * 
 * <p>Lifecycle callbacks (@PrePersist and @PreUpdate) automatically set
 * timestamps when entities are created or updated.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderItem
 * @see OrderStatus
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Primary key identifier for the order.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the user who placed the order.
     * This references a user in the User Service (external service).
     * Cannot be null.
     */
    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * List of items in this order.
     * One-to-many relationship with OrderItem entity.
     * Cascade operations ensure that order items are persisted/deleted with the order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * Total amount of the order.
     * Calculated as the sum of all order items.
     * Stored with precision 10 and scale 2 (e.g., 99999999.99).
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Current status of the order.
     * Defaults to PENDING when order is created.
     * 
     * @see OrderStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Timestamp indicating when the order was created.
     * Automatically set by @PrePersist lifecycle callback.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the order was last updated.
     * Automatically updated by @PreUpdate lifecycle callback.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback method executed before persisting a new entity.
     * 
     * <p>This method is automatically called by JPA before a new Order entity
     * is persisted to the database. It sets both createdAt and updatedAt
     * timestamps to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    /**
     * Lifecycle callback method executed before updating an existing entity.
     * 
     * <p>This method is automatically called by JPA before an existing Order entity
     * is updated in the database. It updates the updatedAt timestamp to the
     * current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Adds an order item to this order.
     * 
     * <p>This helper method ensures bidirectional relationship consistency
     * between Order and OrderItem entities.
     * 
     * @param item The order item to add
     */
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    /**
     * Calculates and updates the total amount of the order.
     * 
     * <p>This method iterates through all order items and sums their
     * individual totals (price * quantity) to calculate the order total.
     */
    public void calculateTotal() {
        this.totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

