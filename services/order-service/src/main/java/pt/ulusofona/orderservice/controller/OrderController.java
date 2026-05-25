package pt.ulusofona.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ulusofona.orderservice.dto.OrderRequest;
import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST Controller for Order management operations.
 * 
 * <p>This controller provides RESTful API endpoints for managing orders in the system.
 * All endpoints are prefixed with "/orders" and handle HTTP requests for:
 * <ul>
 *   <li>Creating new orders</li>
 *   <li>Retrieving all orders</li>
 *   <li>Retrieving an order by ID</li>
 *   <li>Retrieving orders by user ID</li>
 *   <li>Updating order status</li>
 * </ul>
 * 
 * <p>The controller uses Spring's @RestController annotation, which combines
 * @Controller and @ResponseBody, automatically serializing response objects to JSON.
 * 
 * <p>Order creation involves:
 * <ul>
 *   <li>Synchronous validation via OpenFeign (User Service, Product Service)</li>
 *   <li>Asynchronous event publishing via Kafka (OrderCreatedEvent)</li>
 * </ul>
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderService
 * @see OrderRequest
 * @see OrderResponse
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "API endpoints for managing orders with Kafka and OpenFeign")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order in the system.
     * 
     * <p>This endpoint validates the order request, creates the order, and publishes
     * a Kafka event. Validation includes:
     * <ul>
     *   <li>User existence (via OpenFeign call to User Service)</li>
     *   <li>Product existence and stock availability (via OpenFeign call to Product Service)</li>
     * </ul>
     * 
     * @param request OrderRequest containing user ID and order items
     * @return ResponseEntity containing the created OrderResponse object with HTTP 201 status
     * @apiNote POST /orders
     */
    @Operation(summary = "Create a new order", description = "Creates a new order with validation via OpenFeign and publishes Kafka event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, user not found, product not found, or insufficient stock")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Order data", required = true) @Valid @RequestBody OrderRequest request) {
        OrderResponse createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Retrieves all orders from the system.
     * 
     * @return ResponseEntity containing a list of OrderResponse objects with HTTP 200 status
     * @apiNote GET /orders
     */
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves a specific order by its unique identifier.
     * 
     * @param id The unique identifier of the order to retrieve
     * @return ResponseEntity containing a OrderResponse object with HTTP 200 status
     * @apiNote GET /orders/{id}
     */
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Retrieves all orders for a specific user.
     * 
     * @param userId The ID of the user
     * @return ResponseEntity containing a list of OrderResponse objects with HTTP 200 status
     * @apiNote GET /orders/user/{userId}
     */
    @Operation(summary = "Get orders by user ID", description = "Retrieves all orders for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user orders")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Updates the status of an order.
     * 
     * <p>This endpoint updates the order status and publishes a Kafka event
     * (OrderStatusChangedEvent) to notify other services of the change.
     * 
     * @param id The unique identifier of the order
     * @param status The new status to set
     * @return ResponseEntity containing the updated OrderResponse object with HTTP 200 status
     * @apiNote PUT /orders/{id}/status
     */
    @Operation(summary = "Update order status", description = "Updates the status of an order and publishes Kafka event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @Parameter(description = "New order status", required = true) @RequestParam OrderStatus status) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }
}

