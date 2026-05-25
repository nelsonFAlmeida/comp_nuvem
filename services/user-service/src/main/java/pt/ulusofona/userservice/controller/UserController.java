package pt.ulusofona.userservice.controller;

import pt.ulusofona.userservice.dto.UserRequest;
import pt.ulusofona.userservice.dto.UserResponse;
import pt.ulusofona.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST Controller for User management operations.
 * 
 * <p>This controller provides RESTful API endpoints for managing users in the system.
 * All endpoints are prefixed with "/users" and handle HTTP requests for:
 * <ul>
 *   <li>Retrieving all users</li>
 *   <li>Retrieving a user by ID</li>
 *   <li>Creating a new user</li>
 *   <li>Updating an existing user</li>
 *   <li>Deleting a user</li>
 * </ul>
 * 
 * <p>The controller uses Spring's @RestController annotation, which combines
 * @Controller and @ResponseBody, automatically serializing response objects to JSON.
 * 
 * <p>Input validation is performed using Jakarta Validation annotations on the
 * DTO classes. Invalid requests are handled by the GlobalExceptionHandler.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see UserService
 * @see UserRequest
 * @see UserResponse
 * @see GlobalExceptionHandler
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API endpoints for managing users")
public class UserController {

    /**
     * Service layer dependency for user business logic.
     * Injected via constructor using Lombok's @RequiredArgsConstructor.
     */
    private final UserService userService;

    /**
     * Retrieves all users from the system.
     * 
     * <p>This endpoint returns a list of all users currently stored in the database.
     * The response is returned as a JSON array of UserResponse objects.
     * 
     * @return ResponseEntity containing a list of UserResponse objects with HTTP 200 status
     * @apiNote GET /users
     * @example
     * <pre>
     * GET /users
     * Response: 200 OK
     * [
     *   {
     *     "id": 1,
     *     "name": "John Doe",
     *     "email": "john@example.com",
     *     "createdAt": "2024-01-01T10:00:00",
     *     "updatedAt": "2024-01-01T10:00:00"
     *   }
     * ]
     * </pre>
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * 
     * <p>This endpoint returns a single user if found, or throws an exception
     * if the user does not exist (handled by GlobalExceptionHandler).
     * 
     * @param id The unique identifier of the user to retrieve
     * @return ResponseEntity containing a UserResponse object with HTTP 200 status
     * @throws RuntimeException if user with the given ID is not found
     * @apiNote GET /users/{id}
     * @example
     * <pre>
     * GET /users/1
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "name": "John Doe",
     *   "email": "john@example.com",
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T10:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Creates a new user in the system.
     * 
     * <p>This endpoint accepts a UserRequest object containing user information,
     * validates it, and creates a new user. The request body is validated using
     * Jakarta Validation annotations.
     * 
     * <p>If the email already exists, a RuntimeException is thrown and handled
     * by the GlobalExceptionHandler.
     * 
     * @param request UserRequest object containing user data (name and email)
     * @return ResponseEntity containing the created UserResponse object with HTTP 201 status
     * @throws RuntimeException if email already exists in the system
     * @apiNote POST /users
     * @example
     * <pre>
     * POST /users
     * Request Body:
     * {
     *   "name": "Jane Doe",
     *   "email": "jane@example.com"
     * }
     * Response: 201 CREATED
     * {
     *   "id": 2,
     *   "name": "Jane Doe",
     *   "email": "jane@example.com",
     *   "createdAt": "2024-01-01T11:00:00",
     *   "updatedAt": "2024-01-01T11:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "User data", required = true) @Valid @RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates an existing user's information.
     * 
     * <p>This endpoint updates the user with the specified ID. The request body
     * contains the new user data. Both the user ID and request body are validated.
     * 
     * <p>If the user does not exist, a RuntimeException is thrown. If the new
     * email already exists for another user, a RuntimeException is also thrown.
     * 
     * @param id The unique identifier of the user to update
     * @param request UserRequest object containing updated user data
     * @return ResponseEntity containing the updated UserResponse object with HTTP 200 status
     * @throws RuntimeException if user is not found or email is already in use
     * @apiNote PUT /users/{id}
     * @example
     * <pre>
     * PUT /users/1
     * Request Body:
     * {
     *   "name": "John Updated",
     *   "email": "john.updated@example.com"
     * }
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "name": "John Updated",
     *   "email": "john.updated@example.com",
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T12:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true) @Valid @RequestBody UserRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user from the system.
     * 
     * <p>This endpoint permanently removes the user with the specified ID from
     * the database. If the user does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the user to delete
     * @return ResponseEntity with no content and HTTP 204 status
     * @throws RuntimeException if user with the given ID is not found
     * @apiNote DELETE /users/{id}
     * @example
     * <pre>
     * DELETE /users/1
     * Response: 204 NO CONTENT
     * </pre>
     */
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
