package pt.ulusofona.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for User responses.
 * 
 * <p>This class is used to send user data in HTTP responses. It contains
 * all relevant user information including timestamps. The DTO pattern
 * allows us to control exactly what data is exposed to clients, hiding
 * internal implementation details.
 * 
 * <p>This DTO includes:
 * <ul>
 *   <li>id - User's unique identifier</li>
 *   <li>name - User's full name</li>
 *   <li>email - User's email address</li>
 *   <li>createdAt - Timestamp when user was created</li>
 *   <li>updatedAt - Timestamp when user was last updated</li>
 * </ul>
 * 
 * <p>The response is automatically serialized to JSON by Spring when
 * returned from a REST controller.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see UserRequest
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    /**
     * User's unique identifier.
     * Primary key from the database.
     */
    private Long id;
    
    /**
     * User's full name.
     */
    private String name;
    
    /**
     * User's email address.
     * This should be unique across all users.
     */
    private String email;
    
    /**
     * Timestamp indicating when the user was created.
     * Automatically set when the user is first persisted.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp indicating when the user was last updated.
     * Automatically updated whenever the user entity is modified.
     */
    private LocalDateTime updatedAt;
}
