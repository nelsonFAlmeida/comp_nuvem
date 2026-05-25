package pt.ulusofona.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing a User in the system.
 * 
 * <p>This class maps to the "users" table in the database and represents
 * a user entity with the following attributes:
 * <ul>
 *   <li>id - Primary key, auto-generated</li>
 *   <li>name - User's full name (required, cannot be blank)</li>
 *   <li>email - User's email address (required, must be valid email format, unique)</li>
 *   <li>createdAt - Timestamp when the user was created</li>
 *   <li>updatedAt - Timestamp when the user was last updated</li>
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
 * @see jakarta.persistence.Entity
 * @see jakarta.validation.constraints.Email
 * @see jakarta.validation.constraints.NotBlank
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Primary key identifier for the user.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's full name.
     * Cannot be null or blank. Validated using Jakarta Validation.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String name;

    /**
     * User's email address.
     * Must be a valid email format and unique across all users.
     * Cannot be null or blank.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Timestamp indicating when the user was created.
     * Automatically set by @PrePersist lifecycle callback.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the user was last updated.
     * Automatically updated by @PreUpdate lifecycle callback.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback method executed before persisting a new entity.
     * 
     * <p>This method is automatically called by JPA before a new User entity
     * is persisted to the database. It sets both createdAt and updatedAt
     * timestamps to the current date and time.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback method executed before updating an existing entity.
     * 
     * <p>This method is automatically called by JPA before an existing User entity
     * is updated in the database. It updates the updatedAt timestamp to the
     * current date and time.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
