package pt.ulusofona.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with the User Service.
 * 
 * <p>This interface defines the contract for synchronous HTTP communication
 * with the User Service. OpenFeign automatically generates an implementation
 * of this interface at runtime.
 * 
 * <p>The client is used to:
 * <ul>
 *   <li>Validate that a user exists before creating an order</li>
 *   <li>Fetch user information when needed</li>
 * </ul>
 * 
 * <p>The base URL is configured in application.yml under services.user.url.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.cloud.openfeign.FeignClient
 */
@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserServiceClient {

    /**
     * Retrieves user information by ID.
     * 
     * <p>This method makes a synchronous HTTP GET request to the User Service
     * to fetch user information. If the user does not exist, the User Service
     * will return an error, which will be handled by the Feign error decoder.
     * 
     * @param id The unique identifier of the user
     * @return UserResponse containing user information
     * @apiNote GET /users/{id}
     * @example
     * <pre>
     * GET http://localhost:8081/users/1
     * Response:
     * {
     *   "id": 1,
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T10:00:00"
     * }
     * </pre>
     */
    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable Long id);
}

