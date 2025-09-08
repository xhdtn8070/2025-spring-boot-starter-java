package org.tikim.sample.global.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private String host;
    private int port;
    private String password;  // nullable
    private int timeout = 3000;
    private boolean useSsl = false;
}
