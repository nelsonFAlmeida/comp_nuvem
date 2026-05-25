package pt.ulusofona.productservice.controller;

import pt.ulusofona.productservice.dto.ProductRequest;
import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST Controller for Product management operations.
 * 
 * <p>This controller provides RESTful API endpoints for managing products in the system.
 * All endpoints are prefixed with "/products" and handle HTTP requests for:
 * <ul>
 *   <li>Retrieving all products</li>
 *   <li>Retrieving a product by ID</li>
 *   <li>Searching products by name</li>
 *   <li>Creating a new product</li>
 *   <li>Updating an existing product</li>
 *   <li>Deleting a product</li>
 * </ul>
 * 
 * <p>The controller uses Spring's @RestController annotation, which combines
 * @Controller and @ResponseBody, automatically serializing response objects to JSON.
 * 
 * <p>Input validation is performed using Jakarta Validation annotations on the
 * DTO classes. Invalid requests are handled by the GlobalExceptionHandler.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see ProductService
 * @see ProductRequest
 * @see ProductResponse
 * @see GlobalExceptionHandler
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "API endpoints for managing products")
public class ProductController {

    /**
     * Service layer dependency for product business logic.
     * Injected via constructor using Lombok's @RequiredArgsConstructor.
     */
    private final ProductService productService;

    /**
     * Retrieves all products from the system.
     * 
     * <p>This endpoint returns a list of all products currently stored in the database.
     * The response is returned as a JSON array of ProductResponse objects.
     * 
     * @return ResponseEntity containing a list of ProductResponse objects with HTTP 200 status
     * @apiNote GET /products
     * @example
     * <pre>
     * GET /products
     * Response: 200 OK
     * [
     *   {
     *     "id": 1,
     *     "name": "Laptop",
     *     "description": "High-performance laptop",
     *     "price": 999.99,
     *     "stockQuantity": 10,
     *     "createdAt": "2024-01-01T10:00:00",
     *     "updatedAt": "2024-01-01T10:00:00"
     *   }
     * ]
     * </pre>
     */
    @Operation(summary = "Get all products", description = "Retrieves a list of all products in the system")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products")
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Retrieves a specific product by its unique identifier.
     * 
     * <p>This endpoint returns a single product if found, or throws an exception
     * if the product does not exist (handled by GlobalExceptionHandler).
     * 
     * @param id The unique identifier of the product to retrieve
     * @return ResponseEntity containing a ProductResponse object with HTTP 200 status
     * @throws RuntimeException if product with the given ID is not found
     * @apiNote GET /products/{id}
     * @example
     * <pre>
     * GET /products/1
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "name": "Laptop",
     *   "description": "High-performance laptop",
     *   "price": 999.99,
     *   "stockQuantity": 10,
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T10:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Searches for products by name (case-insensitive partial match).
     * 
     * <p>This endpoint searches for products whose name contains the specified
     * search term. The search is case-insensitive and performs a partial match.
     * 
     * @param name The search term to match against product names
     * @return ResponseEntity containing a list of matching ProductResponse objects with HTTP 200 status
     * @apiNote GET /products/search?name={searchTerm}
     * @example
     * <pre>
     * GET /products/search?name=laptop
     * Response: 200 OK
     * [
     *   {
     *     "id": 1,
     *     "name": "Laptop",
     *     "description": "High-performance laptop",
     *     "price": 999.99,
     *     "stockQuantity": 10,
     *     "createdAt": "2024-01-01T10:00:00",
     *     "updatedAt": "2024-01-01T10:00:00"
     *   }
     * ]
     * </pre>
     */
    @Operation(summary = "Search products by name", description = "Searches for products by name (case-insensitive partial match)")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @Parameter(description = "Search term", required = true) @RequestParam String name) {
        List<ProductResponse> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Creates a new product in the system.
     * 
     * <p>This endpoint accepts a ProductRequest object containing product information,
     * validates it, and creates a new product. The request body is validated using
     * Jakarta Validation annotations.
     * 
     * @param request ProductRequest object containing product data (name, description, price, stockQuantity)
     * @return ResponseEntity containing the created ProductResponse object with HTTP 201 status
     * @apiNote POST /products
     * @example
     * <pre>
     * POST /products
     * Request Body:
     * {
     *   "name": "Smartphone",
     *   "description": "Latest smartphone model",
     *   "price": 699.99,
     *   "stockQuantity": 25
     * }
     * Response: 201 CREATED
     * {
     *   "id": 2,
     *   "name": "Smartphone",
     *   "description": "Latest smartphone model",
     *   "price": 699.99,
     *   "stockQuantity": 25,
     *   "createdAt": "2024-01-01T11:00:00",
     *   "updatedAt": "2024-01-01T11:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "Product data", required = true) @Valid @RequestBody ProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Updates an existing product's information.
     * 
     * <p>This endpoint updates the product with the specified ID. The request body
     * contains the new product data. Both the product ID and request body are validated.
     * 
     * <p>If the product does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the product to update
     * @param request ProductRequest object containing updated product data
     * @return ResponseEntity containing the updated ProductResponse object with HTTP 200 status
     * @throws RuntimeException if product is not found
     * @apiNote PUT /products/{id}
     * @example
     * <pre>
     * PUT /products/1
     * Request Body:
     * {
     *   "name": "Laptop Updated",
     *   "description": "Updated description",
     *   "price": 1099.99,
     *   "stockQuantity": 15
     * }
     * Response: 200 OK
     * {
     *   "id": 1,
     *   "name": "Laptop Updated",
     *   "description": "Updated description",
     *   "price": 1099.99,
     *   "stockQuantity": 15,
     *   "createdAt": "2024-01-01T10:00:00",
     *   "updatedAt": "2024-01-01T12:00:00"
     * }
     * </pre>
     */
    @Operation(summary = "Update product", description = "Updates an existing product's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id,
            @Parameter(description = "Updated product data", required = true) @Valid @RequestBody ProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deletes a product from the system.
     * 
     * <p>This endpoint permanently removes the product with the specified ID from
     * the database. If the product does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the product to delete
     * @return ResponseEntity with no content and HTTP 204 status
     * @throws RuntimeException if product with the given ID is not found
     * @apiNote DELETE /products/{id}
     * @example
     * <pre>
     * DELETE /products/1
     * Response: 204 NO CONTENT
     * </pre>
     */
    @Operation(summary = "Delete product", description = "Deletes a product from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
