package pt.ulusofona.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the Product Service microservice.
 * 
 * <p>This is the entry point for the Product Service application. It uses Spring Boot
 * auto-configuration to set up the application context, including:
 * <ul>
 *   <li>Spring Data JPA for database access</li>
 *   <li>Spring Web for REST API endpoints</li>
 *   <li>Spring Kafka for consuming events from Order Service</li>
 *   <li>H2 in-memory database for development</li>
 *   <li>Spring Boot Actuator for health checks and monitoring</li>
 * </ul>
 * 
 * <p>The service consumes Kafka events from the Order Service to:
 * <ul>
 *   <li>Update inventory when orders are created</li>
 *   <li>React to order status changes</li>
 * </ul>
 * 
 * <p>The service runs on port 8082 by default (configured in application.yml).
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.kafka.annotation.EnableKafka
 */
@SpringBootApplication
@EnableKafka
public class ProductServiceApplication {

    /**
     * Main method to start the Product Service application.
     * 
     * <p>This method initializes the Spring Boot application context and starts
     * the embedded Tomcat server. The application will be available at
     * http://localhost:8082 once started.
     * 
     * <p>Prerequisites:
     * <ul>
     *   <li>Kafka must be running on localhost:9092 (or configured in application.yml)</li>
     * </ul>
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
