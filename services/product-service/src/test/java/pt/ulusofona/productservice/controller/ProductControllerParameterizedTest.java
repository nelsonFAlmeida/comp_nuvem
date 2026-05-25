package pt.ulusofona.productservice.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = {ProductController.class, GlobalExceptionHandler.class})
class ProductControllerParameterizedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @ParameterizedTest(name = "getProductById id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8})
    void getProductById_WithValidId_Returns200(long id) throws Exception {
        ProductResponse product = new ProductResponse(id, "Product" + id, "Desc", new BigDecimal("99.99"), 10, LocalDateTime.now(), LocalDateTime.now());
        when(productService.getProductById(id)).thenReturn(product);

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Product" + id));

        verify(productService, times(1)).getProductById(id);
    }

    @ParameterizedTest(name = "getProductById not found id={0}")
    @ValueSource(longs = {100, 101, 102, 103, 104, 105, 106, 107})
    void getProductById_WhenNotFound_Returns400(long id) throws Exception {
        when(productService.getProductById(id)).thenThrow(new RuntimeException("Produto não encontrado com ID: " + id));

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));

        verify(productService, times(1)).getProductById(id);
    }

    @ParameterizedTest(name = "deleteProduct id={0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void deleteProduct_WithValidId_Returns204(long id) throws Exception {
        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/products/" + id))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(id);
    }

    @ParameterizedTest(name = "searchProducts name={0}")
    @ValueSource(strings = {"lap", "mouse", "key", "a", "b", "c", "d", "e", "f", "g", "h"})
    void searchProducts_Returns200(String name) throws Exception {
        when(productService.searchProductsByName(name)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products/search").param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(productService, times(1)).searchProductsByName(name);
    }
}
