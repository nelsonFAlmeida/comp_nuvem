package pt.ulusofona.orderservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestTest {

    @ParameterizedTest(name = "userId={0}")
    @ValueSource(longs = {1, 2, 100})
    void constructor_SetsUserId(long userId) {
        OrderItemRequest item = new OrderItemRequest(1L, 1);
        OrderRequest r = new OrderRequest(userId, List.of(item));
        assertEquals(userId, r.getUserId());
        assertEquals(1, r.getItems().size());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        OrderRequest r = new OrderRequest();
        assertNull(r.getUserId());
        assertNull(r.getItems());
    }

    @ParameterizedTest(name = "setUserId {0}")
    @ValueSource(longs = {1, 2, 99})
    void setUserId_SetsValue(long userId) {
        OrderRequest r = new OrderRequest();
        r.setUserId(userId);
        assertEquals(userId, r.getUserId());
    }
}
