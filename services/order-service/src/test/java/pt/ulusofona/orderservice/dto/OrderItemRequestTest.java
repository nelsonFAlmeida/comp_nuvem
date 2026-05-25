package pt.ulusofona.orderservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemRequestTest {

    @ParameterizedTest(name = "productId={0} quantity={1}")
    @CsvSource({"1, 1", "2, 5", "10, 100"})
    void constructor_SetsFields(long productId, int quantity) {
        OrderItemRequest r = new OrderItemRequest(productId, quantity);
        assertEquals(productId, r.getProductId());
        assertEquals(quantity, r.getQuantity());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        OrderItemRequest r = new OrderItemRequest();
        assertNull(r.getProductId());
        assertNull(r.getQuantity());
    }

    @ParameterizedTest(name = "setProductId {0}")
    @ValueSource(longs = {1, 2, 100})
    void setProductId_SetsValue(long productId) {
        OrderItemRequest r = new OrderItemRequest();
        r.setProductId(productId);
        assertEquals(productId, r.getProductId());
    }

    @ParameterizedTest(name = "setQuantity {0}")
    @ValueSource(ints = {1, 2, 10, 100})
    void setQuantity_SetsValue(int quantity) {
        OrderItemRequest r = new OrderItemRequest();
        r.setQuantity(quantity);
        assertEquals(quantity, r.getQuantity());
    }
}
