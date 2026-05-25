package pt.ulusofona.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating a User.
 * 
 * <p>This class is used to receive user data from HTTP requests. It contains
 * validation annotations to ensure data integrity before processing. The DTO
 * pattern separates the API contract from the internal entity model, providing
 * better control over what data is exposed and validated.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>name - Cannot be null or blank</li>
 *   <li>email - Cannot be null or blank, must be a valid email format</li>
 * </ul>
 * 
 * <p>Invalid requests are automatically handled by the GlobalExceptionHandler,
 * which returns appropriate error messages to the client.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see UserResponse
 * @see jakarta.validation.constraints.NotBlank
 * @see jakarta.validation.constraints.Email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    /**
     * User's full name.
     * 
     * <p>This field is required and cannot be null or blank. The validation
     * is performed automatically by Spring when the @Valid annotation is
     * used in the controller.
     */
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    /**
     * User's email address.
     * 
     * <p>This field is required, must be a valid email format, and should
     * be unique across all users. The validation ensures:
     * <ul>
     *   <li>Field is not null or blank</li>
     *   <li>Email format is valid (contains @, domain, etc.)</li>
     * </ul>
     * 
     * <p>Note: Uniqueness validation is performed at the service layer,
     * not at the DTO level.
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
}
