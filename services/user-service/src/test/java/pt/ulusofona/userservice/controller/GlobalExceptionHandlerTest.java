package pt.ulusofona.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequestWithMessage() {
        RuntimeException ex = new RuntimeException("Utilizador não encontrado com ID: 999");

        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Utilizador não encontrado com ID: 999", response.getBody().get("message"));
        assertEquals("400", response.getBody().get("status"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithFieldErrors() {
        pt.ulusofona.userservice.dto.UserRequest target = new pt.ulusofona.userservice.dto.UserRequest("", "");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "userRequest");
        bindingResult.rejectValue("name", "NotBlank", "Nome é obrigatório");
        bindingResult.rejectValue("email", "Email", "Email deve ser válido");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nome é obrigatório", response.getBody().get("name"));
        assertEquals("Email deve ser válido", response.getBody().get("email"));
        assertEquals("400", response.getBody().get("status"));
    }

    @ParameterizedTest(name = "handleRuntimeException message={0}")
    @ValueSource(strings = {
            "Utilizador não encontrado com ID: 1", "Utilizador não encontrado com ID: 2",
            "Email já está em uso: a@a.com", "Email já está em uso: b@b.com",
            "Error 1", "Error 2", "Error 3", "Not found 1", "Not found 2"
    })
    void handleRuntimeException_VariousMessages_Returns400WithMessage(String message) {
        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(new RuntimeException(message));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody().get("message"));
        assertEquals("400", response.getBody().get("status"));
    }
}
