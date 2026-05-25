package pt.ulusofona.productservice.controller;

import pt.ulusofona.productservice.dto.ProductRequest;
import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@org.springframework.context.annotation.Import(pt.ulusofona.productservice.controller.GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        // Arrange
        ProductResponse product1 = new ProductResponse(1L, "Notebook", "Descrição 1", 
                new BigDecimal("999.99"), 10, LocalDateTime.now(), LocalDateTime.now());
        ProductResponse product2 = new ProductResponse(2L, "Mouse", "Descrição 2", 
                new BigDecimal("29.99"), 20, LocalDateTime.now(), LocalDateTime.now());
        List<ProductResponse> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Notebook"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        // Arrange
        ProductResponse product = new ProductResponse(1L, "Notebook", "Descrição", 
                new BigDecimal("999.99"), 10, LocalDateTime.now(), LocalDateTime.now());

        when(productService.getProductById(1L)).thenReturn(product);

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.price").value(999.99));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Notebook", "Descrição", 
                new BigDecimal("999.99"), 10);
        ProductResponse response = new ProductResponse(1L, "Notebook", "Descrição", 
                new BigDecimal("999.99"), 10, LocalDateTime.now(), LocalDateTime.now());

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Notebook"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Notebook Atualizado", "Nova descrição", 
                new BigDecimal("1099.99"), 5);
        ProductResponse response = new ProductResponse(1L, "Notebook Atualizado", "Nova descrição", 
                new BigDecimal("1099.99"), 5, LocalDateTime.now(), LocalDateTime.now());

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook Atualizado"));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturn400WithMessage() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new RuntimeException("Produto não encontrado com ID: 999"));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Produto não encontrado com ID: 999"))
                .andExpect(jsonPath("$.status").value("400"));

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void searchProducts_ShouldReturnMatchingProducts() throws Exception {
        ProductResponse p1 = new ProductResponse(1L, "Notebook", "Desc", new BigDecimal("999.99"), 10, LocalDateTime.now(), LocalDateTime.now());
        when(productService.searchProductsByName("note")).thenReturn(Arrays.asList(p1));

        mockMvc.perform(get("/products/search").param("name", "note"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Notebook"));

        verify(productService, times(1)).searchProductsByName("note");
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldReturn400WithMessage() throws Exception {
        doThrow(new RuntimeException("Produto não encontrado com ID: 999")).when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Produto não encontrado com ID: 999"))
                .andExpect(jsonPath("$.status").value("400"));

        verify(productService, times(1)).deleteProduct(999L);
    }
}

