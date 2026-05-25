package pt.ulusofona.orderservice.controller;

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
        RuntimeException ex = new RuntimeException("Order not found with ID: 999");

        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order not found with ID: 999", response.getBody().get("message"));
        assertEquals("400", response.getBody().get("status"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithFieldErrors() {
        pt.ulusofona.orderservice.dto.OrderRequest target = new pt.ulusofona.orderservice.dto.OrderRequest(null, null);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "orderRequest");
        bindingResult.rejectValue("userId", "NotNull", "must not be null");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("must not be null", response.getBody().get("userId"));
        assertEquals("400", response.getBody().get("status"));
    }

    @ParameterizedTest(name = "handleRuntimeException message={0}")
    @ValueSource(strings = {
            "Order not found with ID: 1", "Order not found with ID: 2", "Order not found with ID: 3",
            "User not found with ID: 1", "Product not found with ID: 1",
            "Error 1", "Error 2", "Error 3", "Error 4", "Error 5",
            "Insufficient stock 1", "Insufficient stock 2", "Insufficient stock 3"
    })
    void handleRuntimeException_VariousMessages_Returns400(String message) {
        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(new RuntimeException(message));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody().get("message"));
        assertEquals("400", response.getBody().get("status"));
    }
}
