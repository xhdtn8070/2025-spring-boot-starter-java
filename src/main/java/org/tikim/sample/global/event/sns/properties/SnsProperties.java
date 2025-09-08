package org.tikim.sample.global.event.sns.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.sns")
public class SnsProperties {
    private String region;
    private String endpoint; // nullable
    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}
