package pt.ulusofona.productservice.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @ParameterizedTest(name = "setId {0}")
    @ValueSource(longs = {1, 2, 100})
    void setId_SetsValue(long id) {
        Product p = new Product();
        p.setId(id);
        assertEquals(id, p.getId());
    }

    @ParameterizedTest(name = "setName {0}")
    @ValueSource(strings = {"Laptop", "Mouse", "Keyboard"})
    void setName_SetsValue(String name) {
        Product p = new Product();
        p.setName(name);
        assertEquals(name, p.getName());
    }

    @ParameterizedTest(name = "setStockQuantity {0}")
    @ValueSource(ints = {0, 1, 10, 100})
    void setStockQuantity_SetsValue(int stock) {
        Product p = new Product();
        p.setStockQuantity(stock);
        assertEquals(stock, p.getStockQuantity());
    }

    @ParameterizedTest(name = "allArgsConstructor id={0} name={1}")
    @CsvSource({
            "1, Laptop, 999.99, 10",
            "2, Mouse, 29.99, 50"
    })
    void allArgsConstructor_SetsFields(long id, String name, String price, int stock) {
        Product p = new Product(id, name, "desc", new BigDecimal(price), stock, null, null);
        assertEquals(id, p.getId());
        assertEquals(name, p.getName());
        assertEquals(stock, p.getStockQuantity());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        Product p = new Product();
        assertNull(p.getId());
        assertNull(p.getName());
    }
}
