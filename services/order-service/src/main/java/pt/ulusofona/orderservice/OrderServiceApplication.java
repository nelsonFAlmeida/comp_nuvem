package pt.ulusofona.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the Order Service microservice.
 * 
 * <p>This is the entry point for the Order Service application. It uses Spring Boot
 * auto-configuration to set up the application context, including:
 * <ul>
 *   <li>Spring Data JPA for database access</li>
 *   <li>Spring Web for REST API endpoints</li>
 *   <li>Spring Kafka for asynchronous messaging</li>
 *   <li>OpenFeign for synchronous inter-service communication</li>
 *   <li>H2 in-memory database for development</li>
 *   <li>Spring Boot Actuator for health checks and monitoring</li>
 * </ul>
 * 
 * <p>The service demonstrates two types of inter-service communication:
 * <ul>
 *   <li><b>Synchronous (OpenFeign):</b> Calls to User Service and Product Service
 *       to validate orders and fetch product information</li>
 *   <li><b>Asynchronous (Kafka):</b> Publishes events when orders are created or
 *       status changes, allowing other services to react to these events</li>
 * </ul>
 * 
 * <p>The service runs on port 8083 by default (configured in application.yml).
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.cloud.openfeign.EnableFeignClients
 * @see org.springframework.kafka.annotation.EnableKafka
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class OrderServiceApplication {

    /**
     * Main method to start the Order Service application.
     * 
     * <p>This method initializes the Spring Boot application context and starts
     * the embedded Tomcat server. The application will be available at
     * http://localhost:8083 once started.
     * 
     * <p>Prerequisites:
     * <ul>
     *   <li>Kafka must be running on localhost:9092 (or configured in application.yml)</li>
     *   <li>User Service should be running on localhost:8081</li>
     *   <li>Product Service should be running on localhost:8082</li>
     * </ul>
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

