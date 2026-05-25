package pt.ulusofona.productservice.repository;

import pt.ulusofona.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Product entity database operations.
 * 
 * <p>This interface extends Spring Data JPA's JpaRepository, providing
 * standard CRUD operations and custom query methods. Spring Data JPA
 * automatically generates implementations for the methods defined here.
 * 
 * <p>The repository provides the following operations:
 * <ul>
 *   <li>Standard CRUD operations (inherited from JpaRepository)</li>
 *   <li>findByNameContainingIgnoreCase - Find products by name (case-insensitive partial match)</li>
 *   <li>findByPriceBetween - Find products within a price range</li>
 * </ul>
 * 
 * <p>Spring Data JPA automatically implements these methods based on the
 * method naming convention. For example, "findByNameContainingIgnoreCase"
 * will generate a query like "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)".
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see Product
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds products whose name contains the specified string (case-insensitive).
     * 
     * <p>This method performs a case-insensitive partial match search on product names.
     * For example, searching for "laptop" will match "Laptop", "LAPTOP", "Gaming Laptop", etc.
     * 
     * @param name The search term to match against product names
     * @return List of Product entities matching the search criteria
     * @apiNote Spring Data JPA automatically generates the query:
     *          SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', ?, '%'))
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Finds products within a specified price range.
     * 
     * <p>This method queries the database for products whose price falls between
     * the minimum and maximum values (inclusive).
     * 
     * @param minPrice The minimum price (inclusive)
     * @param maxPrice The maximum price (inclusive)
     * @return List of Product entities within the price range
     * @apiNote Spring Data JPA automatically generates the query:
     *          SELECT * FROM products WHERE price BETWEEN ? AND ?
     */
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}
