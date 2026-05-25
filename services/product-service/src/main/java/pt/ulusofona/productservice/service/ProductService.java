package pt.ulusofona.productservice.service;

import pt.ulusofona.productservice.dto.ProductRequest;
import pt.ulusofona.productservice.dto.ProductResponse;
import pt.ulusofona.productservice.model.Product;
import pt.ulusofona.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class containing business logic for Product operations.
 * 
 * <p>This service layer acts as an intermediary between the controller and repository
 * layers, implementing business logic and transaction management. It handles:
 * <ul>
 *   <li>Retrieving all products</li>
 *   <li>Retrieving a product by ID</li>
 *   <li>Searching products by name</li>
 *   <li>Creating new products</li>
 *   <li>Updating existing products</li>
 *   <li>Deleting products</li>
 *   <li>Mapping between Entity and DTO objects</li>
 * </ul>
 * 
 * <p>All database operations are wrapped in transactions. Read operations use
 * @Transactional(readOnly = true) for better performance, while write operations
 * use @Transactional to ensure data consistency.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see ProductRepository
 * @see Product
 * @see ProductRequest
 * @see ProductResponse
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    /**
     * Repository dependency for database operations.
     * Injected via constructor using Lombok's @RequiredArgsConstructor.
     */
    private final ProductRepository productRepository;

    /**
     * Retrieves all products from the database.
     * 
     * <p>This method fetches all products from the database and converts them
     * to ProductResponse DTOs. The operation is read-only for better performance.
     * 
     * @return List of ProductResponse objects representing all products
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a product by its unique identifier.
     * 
     * <p>This method fetches a product from the database by ID. If the product
     * does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the product to retrieve
     * @return ProductResponse object representing the product
     * @throws RuntimeException if product with the given ID is not found
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        return mapToResponse(product);
    }

    /**
     * Creates a new product in the database.
     * 
     * <p>This method creates a new Product entity, sets its properties from the request,
     * initializes stockQuantity to 0 if not provided, saves it to the database, and
     * returns the created product as a DTO.
     * 
     * @param request ProductRequest object containing product data
     * @return ProductResponse object representing the created product
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    /**
     * Updates an existing product's information.
     * 
     * <p>This method retrieves the product by ID, updates the product's properties,
     * saves the changes, and returns the updated product as a DTO.
     * 
     * @param id The unique identifier of the product to update
     * @param request ProductRequest object containing updated product data
     * @return ProductResponse object representing the updated product
     * @throws RuntimeException if product is not found
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    /**
     * Deletes a product from the database.
     * 
     * <p>This method validates that the product exists before attempting deletion.
     * If the product does not exist, a RuntimeException is thrown.
     * 
     * @param id The unique identifier of the product to delete
     * @throws RuntimeException if product with the given ID is not found
     * @apiNote This method uses a write transaction
     */
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Searches for products by name (case-insensitive partial match).
     * 
     * <p>This method queries the database for products whose name contains the
     * specified search term. The search is case-insensitive and performs a partial
     * match. Results are converted to DTOs.
     * 
     * @param name The search term to match against product names
     * @return List of ProductResponse objects representing matching products
     * @apiNote This is a read-only transaction
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Product entity to a ProductResponse DTO.
     * 
     * <p>This private helper method converts a Product entity object to a ProductResponse
     * DTO object. It extracts all relevant fields from the entity and creates a
     * new DTO instance.
     * 
     * @param product Product entity to convert
     * @return ProductResponse DTO representing the product
     */
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
