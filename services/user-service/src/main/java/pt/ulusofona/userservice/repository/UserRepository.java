package pt.ulusofona.userservice.repository;

import pt.ulusofona.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 * 
 * <p>This interface extends Spring Data JPA's JpaRepository, providing
 * standard CRUD operations and custom query methods. Spring Data JPA
 * automatically generates implementations for the methods defined here.
 * 
 * <p>The repository provides the following operations:
 * <ul>
 *   <li>Standard CRUD operations (inherited from JpaRepository)</li>
 *   <li>findByEmail - Find a user by email address</li>
 *   <li>existsByEmail - Check if a user exists with the given email</li>
 * </ul>
 * 
 * <p>Spring Data JPA automatically implements these methods based on the
 * method naming convention. For example, "findByEmail" will generate
 * a query like "SELECT * FROM users WHERE email = ?".
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see User
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their email address.
     * 
     * <p>This method queries the database for a user with the specified email.
     * The email field is unique, so this method will return at most one user.
     * 
     * @param email The email address to search for
     * @return Optional containing the User if found, empty Optional otherwise
     * @apiNote Spring Data JPA automatically generates the query: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user exists with the given email address.
     * 
     * <p>This method is useful for validation purposes, such as checking
     * email uniqueness before creating a new user.
     * 
     * @param email The email address to check
     * @return true if a user exists with the given email, false otherwise
     * @apiNote Spring Data JPA automatically generates the query: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
}
