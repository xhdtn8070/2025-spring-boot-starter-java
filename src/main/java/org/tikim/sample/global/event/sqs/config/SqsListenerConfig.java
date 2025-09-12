// SqsListenerConfig.java
package org.tikim.sample.global.event.sqs.config;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tikim.sample.global.event.sqs.properties.SqsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.tikim.sample.global.event.sqs.properties")
public class SqsListenerConfig {

    @Bean
    public SqsAsyncClient sqsAsyncClient(SqsProperties props) {
        var b = SqsAsyncClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                props.getCredentials().getAccessKey(),
                                props.getCredentials().getSecretKey()
                        )));
        if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) {
            b.endpointOverride(URI.create(props.getEndpoint()));
        }
        return b.build();
    }

    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient
    ) {
        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(opts -> opts
                        .maxConcurrentMessages(10)            // 동시에 처리할 메시지 슬롯(스레드 유사)
                        .maxMessagesPerPoll(10)               // 1회 폴링에서 가져오는 최대 수
                        .pollTimeout(Duration.ofSeconds(20))  // Long polling
                        .acknowledgementMode(AcknowledgementMode.ON_SUCCESS) // 성공 시 자동 삭제
                        .acknowledgementInterval(Duration.ofSeconds(5))      // 배치 ACK 간격
                        .acknowledgementThreshold(10)         // 배치 ACK 최소 개수
                )
                .build();
    }

    // (선택) 보내기용
    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.newTemplate(sqsAsyncClient);
    }
}
