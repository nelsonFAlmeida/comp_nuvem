package pt.ulusofona.orderservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResponseTest {

    @ParameterizedTest(name = "id={0} productId={1} quantity={2}")
    @CsvSource({"1, 1, 2", "2, 2, 5"})
    void constructor_SetsFields(long id, long productId, int quantity) {
        OrderItemResponse r = new OrderItemResponse(id, productId, "P", quantity, BigDecimal.TEN);
        assertEquals(id, r.getId());
        assertEquals(productId, r.getProductId());
        assertEquals(quantity, r.getQuantity());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        OrderItemResponse r = new OrderItemResponse();
        assertNull(r.getId());
        assertNull(r.getProductId());
    }

    @ParameterizedTest(name = "setQuantity {0}")
    @ValueSource(ints = {1, 2, 10})
    void setQuantity_SetsValue(int quantity) {
        OrderItemResponse r = new OrderItemResponse();
        r.setQuantity(quantity);
        assertEquals(quantity, r.getQuantity());
    }
}
