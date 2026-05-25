package pt.ulusofona.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the Product Service.
 * 
 * <p>This interface defines the contract for synchronous HTTP communication
 * with the Product Service. OpenFeign automatically generates an implementation
 * of this interface at runtime.
 * 
 * <p>The client is used to:
 * <ul>
 *   <li>Validate that products exist before creating an order</li>
 *   <li>Fetch product information (name, price) to create order items</li>
 *   <li>Check product availability (stock quantity)</li>
 * </ul>
 * 
 * <p>The base URL is configured in application.yml under services.product.url.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.cloud.openfeign.FeignClient
 */
@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductServiceClient {

    /**
     * Retrieves product information by ID.
     * 
     * <p>This method makes a synchronous HTTP GET request to the Product Service
     * to fetch product information. If the product does not exist, the Product
     * Service will return an error, which will be handled by the Feign error decoder.
     * 
     * @param id The unique identifier of the product
     * @return ProductResponse containing product information
     * @apiNote GET /products/{id}
     * @example
     * <pre>
     * GET http://localhost:8082/products/1
     * Response:
     * {
     *   "id": 1,
     *   "name": "Laptop",
     *   "description": "High-performance laptop",
     *   "price": 999.99,
     *   "stockQuantity": 10,
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T10:00:00"
     * }
     * </pre>
     */
    @GetMapping("/products/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}

