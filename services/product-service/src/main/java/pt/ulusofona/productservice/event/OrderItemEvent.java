package pt.ulusofona.productservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents an order item in a Kafka event.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}

