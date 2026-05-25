package pt.ulusofona.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring Cloud Gateway routes.
 * 
 * <p>This class configures the routing rules for the API Gateway, defining
 * how incoming requests are forwarded to backend microservices. The gateway
 * acts as a reverse proxy, routing requests based on URL patterns.
 * 
 * <p>Current routing configuration:
 * <ul>
 *   <li>/api/users/** -> User Service (http://localhost:8081)</li>
 *   <li>/api/products/** -> Product Service (http://localhost:8082)</li>
 * </ul>
 * 
 * <p>Note: When Docker Compose is implemented (Week 2), these URLs should be
 * updated to use service names instead of localhost (e.g., http://user-service:8081).
 * 
 * <p>The routes are configured programmatically using RouteLocatorBuilder, which
 * provides a fluent API for defining routes. Alternatively, routes can be
 * configured in application.yml.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 * @see org.springframework.cloud.gateway.route.RouteLocator
 * @see org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
 */
@Configuration
public class GatewayConfig {

    /**
     * Creates and configures the RouteLocator bean for API Gateway routing.
     * 
     * <p>This method defines the routing rules for the gateway. Each route
     * specifies:
     * <ul>
     *   <li>A unique route ID</li>
     *   <li>A path pattern to match</li>
     *   <li>A destination URI to forward requests to</li>
     * </ul>
     * 
     * <p>The gateway will:
     * <ol>
     *   <li>Match incoming requests against the path patterns</li>
     *   <li>Forward matching requests to the corresponding backend service</li>
     *   <li>Return the response from the backend service to the client</li>
     * </ol>
     * 
     * <p>Example routing:
     * <ul>
     *   <li>Client request: GET http://localhost:8080/api/users/1</li>
     *   <li>Gateway forwards to: GET http://localhost:8081/users/1</li>
     *   <li>Note: The /api prefix is removed when forwarding</li>
     * </ul>
     * 
     * @param builder RouteLocatorBuilder for constructing routes
     * @return RouteLocator containing all configured routes
     * @apiNote When Docker Compose is implemented, update URIs to use service names:
     *          - http://user-service:8081 (instead of http://localhost:8081)
     *          - http://product-service:8082 (instead of http://localhost:8082)
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service routes
                // Routes all requests matching /api/users/** to the User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("http://localhost:8081"))
                
                // Product Service routes
                // Routes all requests matching /api/products/** to the Product Service
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("http://localhost:8082"))
                
                // Order Service routes
                // Routes all requests matching /api/orders/** to the Order Service
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("http://localhost:8083"))
                
                .build();
    }
}
