package pt.ulusofona.productservice.service;

import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceParameterizedTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @ParameterizedTest(name = "getProductById id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getProductById_WhenExists_ReturnsProduct(long id) {
        Product product = new Product(id, "P" + id, "Desc", BigDecimal.TEN, 5, LocalDateTime.now(), LocalDateTime.now());
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("P" + id, result.getName());
        verify(productRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "getProductById id={0} throws")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107, 108})
    void getProductById_WhenNotExists_Throws(long id) {
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(id));

        verify(productRepository, times(1)).findById(id);
    }

    @ParameterizedTest(name = "deleteProduct id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void deleteProduct_WhenExists_Deletes(long id) {
        when(productRepository.existsById(id)).thenReturn(true);
        doNothing().when(productRepository).deleteById(id);

        productService.deleteProduct(id);

        verify(productRepository, times(1)).existsById(id);
        verify(productRepository, times(1)).deleteById(id);
    }

    @ParameterizedTest(name = "searchProductsByName returns size {0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void searchProductsByName_ReturnsCorrectSize(int count) {
        List<Product> products = new java.util.ArrayList<>();
        for (long i = 0; i < count; i++) {
            products.add(new Product(i + 1, "P" + i, "D", BigDecimal.ONE, 1, LocalDateTime.now(), LocalDateTime.now()));
        }
        when(productRepository.findByNameContainingIgnoreCase("x")).thenReturn(products);

        List<ProductResponse> result = productService.searchProductsByName("x");

        assertNotNull(result);
        assertEquals(count, result.size());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase("x");
    }
}
