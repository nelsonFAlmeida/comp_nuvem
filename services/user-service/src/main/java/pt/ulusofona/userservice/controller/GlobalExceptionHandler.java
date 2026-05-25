package pt.ulusofona.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the User Service.
 * 
 * <p>This class provides centralized exception handling for all controllers
 * in the application. It uses Spring's @RestControllerAdvice annotation to
 * intercept exceptions thrown by any controller method and return appropriate
 * HTTP responses.
 * 
 * <p>The handler processes two types of exceptions:
 * <ul>
 *   <li>RuntimeException - General runtime exceptions (e.g., user not found, email already exists)</li>
 *   <li>MethodArgumentNotValidException - Validation errors from Jakarta Validation</li>
 * </ul>
 * 
 * <p>All exceptions are converted to JSON error responses with appropriate
 * HTTP status codes, providing a consistent error format for API clients.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @see org.springframework.web.bind.annotation.ExceptionHandler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles RuntimeException exceptions thrown by controller methods.
     * 
     * <p>This method catches all RuntimeException instances (including
     * custom exceptions) and converts them to a standardized error response.
     * Common scenarios include:
     * <ul>
     *   <li>User not found</li>
     *   <li>Email already in use</li>
     *   <li>Invalid operation</li>
     * </ul>
     * 
     * @param ex The RuntimeException that was thrown
     * @return ResponseEntity containing error details with HTTP 400 (Bad Request) status
     * @apiNote Returns JSON: {"message": "error message", "status": "400"}
     * @example
     * <pre>
     * If user not found:
     * {
     *   "message": "Utilizador não encontrado com ID: 999",
     *   "status": "400"
     * }
     * </pre>
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles validation exceptions from Jakarta Validation.
     * 
     * <p>This method catches MethodArgumentNotValidException which occurs when
     * request body validation fails (e.g., @NotBlank, @Email annotations fail).
     * It extracts all field-level validation errors and returns them in a
     * structured format.
     * 
     * <p>The response includes:
     * <ul>
     *   <li>Field names as keys</li>
     *   <li>Validation error messages as values</li>
     *   <li>Overall status code</li>
     * </ul>
     * 
     * @param ex The MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity containing field-level errors with HTTP 400 (Bad Request) status
     * @apiNote Returns JSON: {"fieldName": "error message", "status": "400"}
     * @example
     * <pre>
     * If email is invalid:
     * {
     *   "email": "Email deve ser válido",
     *   "name": "Nome é obrigatório",
     *   "status": "400"
     * }
     * </pre>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        errors.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.badRequest().body(errors);
    }
}
