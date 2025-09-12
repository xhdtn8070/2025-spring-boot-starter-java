package org.tikim.sample.global.event.sqs.properties;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {

    private String region;
    private String endpoint; // nullable
    private Credentials credentials;

    // 큐 이름 -> URL 매핑 (post-notify, reply-action)
    private Map<String, String> queues;

    @Getter
    @Setter
    public static class Credentials {

        private String accessKey;
        private String secretKey;
    }
}
