package pt.ulusofona.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for OrderServiceApplication.
 * 
 * <p>This test verifies that the Spring Boot application context loads
 * successfully with all required beans and configurations.
 * 
 * @author Cloud Computing Course
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class OrderServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // If this test passes, it means all beans are properly configured
    }
}

