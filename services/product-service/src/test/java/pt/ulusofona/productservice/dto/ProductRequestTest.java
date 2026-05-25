package pt.ulusofona.productservice.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestTest {

    @ParameterizedTest(name = "name={0} desc={1} price={2} stock={3}")
    @CsvSource({
            "Laptop, High performance, 999.99, 10",
            "Mouse, Wireless, 29.99, 50",
            "Keyboard, Mechanical, 89.00, 25"
    })
    void constructor_SetsFields(String name, String desc, String price, int stock) {
        ProductRequest r = new ProductRequest(name, desc, new BigDecimal(price), stock);
        assertEquals(name, r.getName());
        assertEquals(desc, r.getDescription());
        assertEquals(0, new BigDecimal(price).compareTo(r.getPrice()));
        assertEquals(stock, r.getStockQuantity());
    }

    @Test
    void noArgsConstructor_CreatesInstance() {
        ProductRequest r = new ProductRequest();
        assertNull(r.getName());
        assertNull(r.getDescription());
        assertNull(r.getPrice());
        assertNull(r.getStockQuantity());
    }

    @ParameterizedTest(name = "setName {0}")
    @ValueSource(strings = {"A", "Laptop", "Product X"})
    void setName_SetsValue(String name) {
        ProductRequest r = new ProductRequest();
        r.setName(name);
        assertEquals(name, r.getName());
    }

    @ParameterizedTest(name = "setPrice {0}")
    @CsvSource({"1.00", "99.99", "0.01"})
    void setPrice_SetsValue(String price) {
        ProductRequest r = new ProductRequest();
        BigDecimal p = new BigDecimal(price);
        r.setPrice(p);
        assertEquals(0, p.compareTo(r.getPrice()));
    }

    @ParameterizedTest(name = "setStockQuantity {0}")
    @ValueSource(ints = {0, 1, 10, 100})
    void setStockQuantity_SetsValue(int stock) {
        ProductRequest r = new ProductRequest();
        r.setStockQuantity(stock);
        assertEquals(stock, r.getStockQuantity());
    }

    @ParameterizedTest(name = "equals same name={0}")
    @ValueSource(strings = {"A", "B", "C"})
    void equals_SameFields_ReturnsTrue(String name) {
        ProductRequest a = new ProductRequest(name, "d", BigDecimal.ONE, 1);
        ProductRequest b = new ProductRequest(name, "d", BigDecimal.ONE, 1);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
