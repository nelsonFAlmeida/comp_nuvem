package pt.ulusofona.orderservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @ParameterizedTest(name = "valueOf {0}")
    @EnumSource(OrderStatus.class)
    void valueOf_ReturnsCorrectEnum(OrderStatus status) {
        OrderStatus parsed = OrderStatus.valueOf(status.name());
        assertSame(status, parsed);
    }

    @ParameterizedTest(name = "name {0}")
    @EnumSource(OrderStatus.class)
    void name_IsNotEmpty(OrderStatus status) {
        assertNotNull(status.name());
        assertFalse(status.name().isEmpty());
    }

    @Test
    void values_ContainsAllFive() {
        OrderStatus[] values = OrderStatus.values();
        assertEquals(5, values.length);
    }
}
