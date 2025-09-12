package org.tikim.sample.global.event.sqs.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tikim.sample.global.event.sqs.properties.SqsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.tikim.sample.global.event.sqs.properties")
public class SqsConfig {

    @Bean
    public SqsClient sqsClient(SqsProperties props) {
        SqsClientBuilder builder = SqsClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        props.getCredentials().getAccessKey(),
                                        props.getCredentials().getSecretKey()
                                )
                        )
                );

        // endpoint가 설정돼 있을 때만 override
        if (hasText(props.getEndpoint())) {
            builder.endpointOverride(URI.create(props.getEndpoint()));
        }

        return builder.build();
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

}
