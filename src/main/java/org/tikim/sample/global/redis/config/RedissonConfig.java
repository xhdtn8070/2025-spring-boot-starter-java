package org.tikim.sample.global.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tikim.sample.global.redis.properties.RedissonProperties;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.tikim.sample.global.redis.properties")
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedissonProperties properties) {
        String protocol = properties.isUseSsl() ? "rediss://" : "redis://";
        Config config = new Config();
        config.useSingleServer()
                .setAddress(protocol + properties.getHost() + ":" + properties.getPort())
                .setTimeout(properties.getTimeout());

        if (hasText(properties.getPassword())) {
            config.useSingleServer().setPassword(properties.getPassword());
        }

        return Redisson.create(config);
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
