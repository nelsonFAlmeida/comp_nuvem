package pt.ulusofona.productservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.awspring.cloud.sqs.config.SqsBootstrapConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

/**
 * AWS SQS configuration for the product-service consumer.
 *
 * <p>Registers the low-level {@link SqsAsyncClient} (pointing at the endpoint
 * injected via {@code CLOUD_AWS_SQS_ENDPOINT}) and the Jackson {@link ObjectMapper}
 * needed to deserialise incoming SQS messages into event objects.
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
     * Shared Jackson {@link ObjectMapper} with {@link JavaTimeModule} so that
     * {@code LocalDateTime} fields in event payloads serialise/deserialise correctly.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
