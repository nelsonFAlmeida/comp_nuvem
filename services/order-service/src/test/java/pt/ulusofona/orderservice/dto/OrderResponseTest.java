package pt.ulusofona.orderservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @ParameterizedTest(name = "id={0}")
    @ValueSource(longs = {1, 2, 100})
    void constructor_SetsId(long id) {
        OrderResponse r = new OrderResponse(id, 1L, Collections.emptyList(), BigDecimal.TEN, pt.ulusofona.orderservice.model.OrderStatus.PENDING, NOW, NOW);
        assertEquals(id, r.getId());
        assertEquals(1L, r.getUserId());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        OrderResponse r = new OrderResponse();
        assertNull(r.getId());
        assertNull(r.getUserId());
    }

    @ParameterizedTest(name = "setId {0}")
    @ValueSource(longs = {1, 2, 99})
    void setId_SetsValue(long id) {
        OrderResponse r = new OrderResponse();
        r.setId(id);
        assertEquals(id, r.getId());
    }
}
