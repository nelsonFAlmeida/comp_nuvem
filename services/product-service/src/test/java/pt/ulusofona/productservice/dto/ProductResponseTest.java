package pt.ulusofona.productservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    @ParameterizedTest(name = "id={0} name={1}")
    @CsvSource({
            "1, Laptop",
            "2, Mouse",
            "3, Keyboard"
    })
    void constructor_SetsFields(long id, String name) {
        ProductResponse r = new ProductResponse(id, name, "desc", BigDecimal.TEN, 5, NOW, NOW);
        assertEquals(id, r.getId());
        assertEquals(name, r.getName());
        assertEquals(5, r.getStockQuantity());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        ProductResponse r = new ProductResponse();
        assertNull(r.getId());
        assertNull(r.getName());
    }

    @ParameterizedTest(name = "setId {0}")
    @ValueSource(longs = {1, 2, 100})
    void setId_SetsValue(long id) {
        ProductResponse r = new ProductResponse();
        r.setId(id);
        assertEquals(id, r.getId());
    }

    @ParameterizedTest(name = "setStockQuantity {0}")
    @ValueSource(ints = {0, 1, 10, 99})
    void setStockQuantity_SetsValue(int stock) {
        ProductResponse r = new ProductResponse();
        r.setStockQuantity(stock);
        assertEquals(stock, r.getStockQuantity());
    }
}
