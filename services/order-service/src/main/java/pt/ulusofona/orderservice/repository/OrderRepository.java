package pt.ulusofona.orderservice.repository;

import pt.ulusofona.orderservice.model.Order;
import pt.ulusofona.orderservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Order entity database operations.
 * 
 * <p>This interface extends Spring Data JPA's JpaRepository, providing
 * standard CRUD operations and custom query methods. Spring Data JPA
 * automatically generates implementations for the methods defined here.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see Order
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds all orders for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of orders for the user
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * Finds all orders with a specific status.
     * 
     * @param status The order status to filter by
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Finds all orders for a specific user with a specific status.
     * 
     * @param userId The ID of the user
     * @param status The order status to filter by
     * @return List of orders matching both criteria
     */
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}

