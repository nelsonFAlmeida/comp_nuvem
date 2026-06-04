package pt.ulusofona.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

/**
 * Configuration class for AWS SQS producer (replaces KafkaConfig).
 *
 * <p>Configures the {@link SqsAsyncClient} pointing at the endpoint injected
 * via the {@code CLOUD_AWS_SQS_ENDPOINT} environment variable and exposes an
 * {@link SqsTemplate} bean used by {@link pt.ulusofona.orderservice.service.OrderService}
 * to publish order events.
 *
 * @author Cloud Computing Course
 * @version 2.0.0
 * @since 2.0.0
 */
@Configuration
@Import(SqsBootstrapConfiguration.class)
public class SqsConfig {

    @Value("${spring.cloud.aws.sqs.endpoint}")
    private String sqsEndpoint;

    @Value("${spring.cloud.aws.region.static:eu-west-1}")
    private String awsRegion;

    /**
     * Low-level async SQS client wired to the configured endpoint.
     */
    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(sqsEndpoint))
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * High-level template used to send messages. Serialises payloads to JSON
     * via the shared {@link ObjectMapper} (with Java 8 time support).
     */
    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .build();
    }

    /**
     * Shared Jackson {@link ObjectMapper} with {@link JavaTimeModule} to handle
     * {@code LocalDateTime} serialisation of event payloads.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
