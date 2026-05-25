package pt.ulusofona.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the API Gateway microservice.
 * 
 * <p>This is the entry point for the API Gateway application. It uses Spring Boot
 * auto-configuration to set up the application context, including:
 * <ul>
 *   <li>Spring Cloud Gateway for routing and load balancing</li>
 *   <li>Route configuration for forwarding requests to backend services</li>
 *   <li>Spring Boot Actuator for health checks and monitoring</li>
 * </ul>
 * 
 * <p>The API Gateway acts as a single entry point for all client requests, routing
 * them to the appropriate backend microservices (User Service and Product Service).
 * 
 * <p>The service runs on port 8080 by default (configured in application.yml).
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.cloud.gateway.route.RouteLocator
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Main method to start the API Gateway application.
     * 
     * <p>This method initializes the Spring Boot application context and starts
     * the embedded Tomcat server. The application will be available at
     * http://localhost:8080 once started.
     * 
     * <p>The gateway will route requests as follows:
     * <ul>
     *   <li>/api/users/** -> User Service (port 8081)</li>
     *   <li>/api/products/** -> Product Service (port 8082)</li>
     * </ul>
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
