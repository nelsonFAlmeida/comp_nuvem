package pt.ulusofona.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the User Service microservice.
 * 
 * <p>This is the entry point for the User Service application. It uses Spring Boot
 * auto-configuration to set up the application context, including:
 * <ul>
 *   <li>Spring Data JPA for database access</li>
 *   <li>Spring Web for REST API endpoints</li>
 *   <li>H2 in-memory database for development</li>
 *   <li>Spring Boot Actuator for health checks and monitoring</li>
 * </ul>
 * 
 * <p>The service runs on port 8081 by default (configured in application.yml).
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method to start the User Service application.
     * 
     * <p>This method initializes the Spring Boot application context and starts
     * the embedded Tomcat server. The application will be available at
     * http://localhost:8081 once started.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
