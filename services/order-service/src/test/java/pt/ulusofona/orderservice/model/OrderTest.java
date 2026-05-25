package pt.ulusofona.orderservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order entity (addOrderItem, calculateTotal).
 */
class OrderTest {

    @Test
    void addOrderItem_ShouldAddItemAndSetBidirectionalReference() {
        Order order = new Order();
        order.setUserId(1L);

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setProductName("Laptop");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("999.99"));

        order.addOrderItem(item);

        assertEquals(1, order.getOrderItems().size());
        assertSame(item, order.getOrderItems().get(0));
        assertSame(order, item.getOrder());
    }

    @Test
    void calculateTotal_ShouldSumItemTotals() {
        Order order = new Order();
        order.setUserId(1L);

        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setProductName("Laptop");
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("999.99"));
        order.addOrderItem(item1);

        OrderItem item2 = new OrderItem();
        item2.setProductId(2L);
        item2.setProductName("Mouse");
        item2.setQuantity(1);
        item2.setPrice(new BigDecimal("29.99"));
        order.addOrderItem(item2);

        order.calculateTotal();

        assertEquals(0, new BigDecimal("2029.97").compareTo(order.getTotalAmount()));
    }
}
