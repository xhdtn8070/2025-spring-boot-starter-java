package org.tikim.sample.global.event.sns.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tikim.sample.global.event.sns.properties.SnsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

import java.net.URI;
import software.amazon.awssdk.services.sns.SnsClientBuilder;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.tikim.sample.global.event.sns.properties")
public class SnsConfig {

    @Bean
    public SnsClient snsClient(SnsProperties props) {

        SnsClientBuilder builder = SnsClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        props.getCredentials().getAccessKey(),
                                        props.getCredentials().getSecretKey()
                                )
                        )
                );

        // endpoint가 설정되어 있을 때만 override
        if (hasText(props.getEndpoint())) {
            builder.endpointOverride(URI.create(props.getEndpoint()));
        }

        return builder.build();
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
