package pt.ulusofona.userservice.service;

import pt.ulusofona.userservice.dto.UserRequest;
import pt.ulusofona.userservice.dto.UserResponse;
import pt.ulusofona.userservice.model.User;
import pt.ulusofona.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for User operations.
 * 
 * <p>This service layer acts as an intermediary between the controller and repository
 * layers, implementing business logic and transaction management. It handles:
 * <ul>
 *   <li>Retrieving all users</li>
 *   <li>Retrieving a user by ID</li>
 *   <li>Creating new users with email uniqueness validation</li>
 *   <li>Updating existing users</li>
 *   <li>Deleting users</li>
 *   <li>Mapping between Entity and DTO objects</li>
 * </ul>
 * 
 * <p>All database operations are wrapped in transactions. Read operations use
 * @Transactional(readOnly = true) for better performance, while write operations
 * use @Transactional to ensure data consistency.
 * 
 * <p>The service validates business rules such as email uniqueness before
 * performing database operations.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see UserRepository
 * @see User
 * @see UserRequest
 * @see UserResponse
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * Repository dependency for database operations.
     * Injected via constructor using Lombok's @RequiredArgsConstructor.
     */
    private final UserRepository userRepository;

    /**
     * Retrieves all users from the database.
     * 
     * <p>This method fetches all users from the database and converts them
     * to UserResponse DTOs. The operation is read-only for better performance.
     * 
     * @return List of UserResponse objects representing all users
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their unique identifier.
     * 
     * <p>This method fetches a user from the database by ID. If the user
     * does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the user to retrieve
     * @return UserResponse object representing the user
     * @throws RuntimeException if user with the given ID is not found
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado com ID: " + id));
        return mapToResponse(user);
    }

    /**
     * Creates a new user in the database.
     * 
     * <p>This method validates that the email is unique before creating the user.
     * If the email already exists, a RuntimeException is thrown. The method creates
     * a new User entity, sets its properties from the request, saves it to the
     * database, and returns the created user as a DTO.
     * 
     * @param request UserRequest object containing user data (name and email)
     * @return UserResponse object representing the created user
     * @throws RuntimeException if email already exists in the system
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    /**
     * Updates an existing user's information.
     * 
     * <p>This method retrieves the user by ID, validates that the new email
     * (if changed) is unique, updates the user's properties, saves the changes,
     * and returns the updated user as a DTO.
     * 
     * @param id The unique identifier of the user to update
     * @param request UserRequest object containing updated user data
     * @return UserResponse object representing the updated user
     * @throws RuntimeException if user is not found or email is already in use by another user
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado com ID: " + id));

        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    /**
     * Deletes a user from the database.
     * 
     * <p>This method validates that the user exists before attempting deletion.
     * If the user does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the user to delete
     * @throws RuntimeException if user with the given ID is not found
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilizador não encontrado com ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Maps a User entity to a UserResponse DTO.
     * 
     * <p>This private helper method converts a User entity object to a UserResponse
     * DTO object. It extracts all relevant fields from the entity and creates a
     * new DTO instance.
     * 
     * @param user User entity to convert
     * @return UserResponse DTO representing the user
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
