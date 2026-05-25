package pt.ulusofona.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulusofona.orderservice.client.ProductResponse;
import pt.ulusofona.orderservice.client.ProductServiceClient;
import pt.ulusofona.orderservice.client.UserResponse;
import pt.ulusofona.orderservice.client.UserServiceClient;
import pt.ulusofona.orderservice.dto.OrderItemRequest;
import pt.ulusofona.orderservice.dto.OrderItemResponse;
import pt.ulusofona.orderservice.dto.OrderRequest;
import pt.ulusofona.orderservice.dto.OrderResponse;
import pt.ulusofona.orderservice.event.OrderCreatedEvent;
import pt.ulusofona.orderservice.event.OrderItemEvent;
import pt.ulusofona.orderservice.event.OrderStatusChangedEvent;
import pt.ulusofona.orderservice.model.Order;
import pt.ulusofona.orderservice.model.OrderItem;
import pt.ulusofona.orderservice.model.OrderStatus;
import pt.ulusofona.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for Order operations.
 * 
 * <p>This service layer acts as an intermediary between the controller and repository
 * layers, implementing business logic and transaction management. It handles:
 * <ul>
 *   <li>Creating orders with validation via OpenFeign</li>
 *   <li>Updating order status</li>
 *   <li>Retrieving orders</li>
 *   <li>Publishing Kafka events for order lifecycle</li>
 * </ul>
 * 
 * <p>The service uses OpenFeign clients to communicate synchronously with:
 * <ul>
 *   <li>User Service - to validate user existence</li>
 *   <li>Product Service - to validate products and fetch product details</li>
 * </ul>
 * 
 * <p>The service publishes Kafka events for:
 * <ul>
 *   <li>Order creation - published to "order-created" topic</li>
 *   <li>Status changes - published to "order-status-changed" topic</li>
 * </ul>
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see OrderRepository
 * @see UserServiceClient
 * @see ProductServiceClient
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String ORDER_CREATED_TOPIC = "order-created";
    private static final String ORDER_STATUS_CHANGED_TOPIC = "order-status-changed";

    /**
     * Creates a new order in the database.
     * 
     * <p>This method:
     * <ol>
     *   <li>Validates user exists using UserServiceClient (OpenFeign)</li>
     *   <li>Validates products exist and fetches details using ProductServiceClient (OpenFeign)</li>
     *   <li>Creates order items with product snapshots</li>
     *   <li>Saves the order to the database</li>
     *   <li>Publishes OrderCreatedEvent to Kafka</li>
     * </ol>
     * 
     * @param request OrderRequest containing user ID and order items
     * @return OrderResponse representing the created order
     * @throws RuntimeException if user or product validation fails
     * @apiNote This method uses a write transaction and publishes Kafka events
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user ID: {}", request.getUserId());

        // Validate user exists using OpenFeign (synchronous call)
        try {
            UserResponse user = userServiceClient.getUserById(request.getUserId());
            log.debug("User validated: {}", user.getName());
        } catch (Exception e) {
            log.error("User validation failed for user ID: {}", request.getUserId(), e);
            throw new RuntimeException("User not found with ID: " + request.getUserId());
        }

        // Create order entity
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);

        // Process each order item
        for (OrderItemRequest itemRequest : request.getItems()) {
            // Validate product exists and fetch details using OpenFeign (synchronous call)
            ProductResponse product;
            try {
                product = productServiceClient.getProductById(itemRequest.getProductId());
                log.debug("Product validated: {} - Price: {}", product.getName(), product.getPrice());
            } catch (Exception e) {
                log.error("Product validation failed for product ID: {}", itemRequest.getProductId(), e);
                throw new RuntimeException("Product not found with ID: " + itemRequest.getProductId());
            }

            // Check stock availability
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException(
                    String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                        product.getName(), product.getStockQuantity(), itemRequest.getQuantity()));
            }

            // Create order item with product snapshot
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addOrderItem(orderItem);
        }

        // Calculate total
        order.calculateTotal();

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // Publish Kafka event (asynchronous)
        publishOrderCreatedEvent(savedOrder);

        return mapToResponse(savedOrder);
    }

    /**
     * Retrieves all orders from the database.
     * 
     * @return List of OrderResponse objects representing all orders
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an order by its unique identifier.
     * 
     * @param id The unique identifier of the order to retrieve
     * @return OrderResponse object representing the order
     * @throws RuntimeException if order with the given ID is not found
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return mapToResponse(order);
    }

    /**
     * Retrieves all orders for a specific user.
     * 
     * @param userId The ID of the user
     * @return List of OrderResponse objects for the user
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of an order.
     * 
     * <p>This method:
     * <ol>
     *   <li>Retrieves the order by ID</li>
     *   <li>Updates the status</li>
     *   <li>Saves the order</li>
     *   <li>Publishes OrderStatusChangedEvent to Kafka</li>
     * </ol>
     * 
     * @param id The unique identifier of the order
     * @param newStatus The new status to set
     * @return OrderResponse object representing the updated order
     * @throws RuntimeException if order is not found
     * @apiNote This method uses a write transaction and publishes Kafka events
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", id, newStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated from {} to {}", id, previousStatus, newStatus);

        // Publish Kafka event (asynchronous)
        publishOrderStatusChangedEvent(updatedOrder, previousStatus);

        return mapToResponse(updatedOrder);
    }

    /**
     * Publishes an OrderCreatedEvent to Kafka.
     * 
     * <p>This method creates an OrderCreatedEvent from the order entity and
     * publishes it to the "order-created" Kafka topic. Other services can
     * subscribe to this topic to react to order creation.
     * 
     * @param order The order that was created
     */
    private void publishOrderCreatedEvent(Order order) {
        try {
            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(),
                    order.getUserId(),
                    order.getOrderItems().stream()
                            .map(item -> new OrderItemEvent(
                                    item.getProductId(),
                                    item.getProductName(),
                                    item.getQuantity(),
                                    item.getPrice()))
                            .collect(Collectors.toList()),
                    order.getTotalAmount(),
                    order.getCreatedAt()
            );

            kafkaTemplate.send(ORDER_CREATED_TOPIC, event);
            log.info("Published OrderCreatedEvent for order ID: {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent for order ID: {}", order.getId(), e);
            // Note: In production, you might want to use a dead letter queue or retry mechanism
        }
    }

    /**
     * Publishes an OrderStatusChangedEvent to Kafka.
     * 
     * <p>This method creates an OrderStatusChangedEvent from the order entity and
     * publishes it to the "order-status-changed" Kafka topic. Other services can
     * subscribe to this topic to react to status changes.
     * 
     * @param order The order whose status changed
     * @param previousStatus The previous status before the change
     */
    private void publishOrderStatusChangedEvent(Order order, OrderStatus previousStatus) {
        try {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                    order.getId(),
                    order.getUserId(),
                    previousStatus,
                    order.getStatus(),
                    LocalDateTime.now()
            );

            kafkaTemplate.send(ORDER_STATUS_CHANGED_TOPIC, event);
            log.info("Published OrderStatusChangedEvent for order ID: {} ({} -> {})",
                    order.getId(), previousStatus, order.getStatus());
        } catch (Exception e) {
            log.error("Failed to publish OrderStatusChangedEvent for order ID: {}", order.getId(), e);
            // Note: In production, you might want to use a dead letter queue or retry mechanism
        }
    }

    /**
     * Maps an Order entity to an OrderResponse DTO.
     * 
     * @param order Order entity to convert
     * @return OrderResponse DTO representing the order
     */
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems() != null ? 
                order.getOrderItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getPrice()))
                        .collect(Collectors.toList()) :
                List.of();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                items,
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}

