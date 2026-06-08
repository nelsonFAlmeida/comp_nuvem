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
// @Configuration Disabled to allow application.yml routes to take precedence
public class GatewayConfig {

    // @Bean Disabled
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
