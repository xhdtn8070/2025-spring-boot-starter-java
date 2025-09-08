package org.tikim.sample.global.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AllowedOriginsConfig {

    // FIXME: 필요하면 properties로 분리
    @Bean
    public String[] allowedOrigins() {
        return new String[] {
                "http://localhost",
                "http://localhost:80",
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:3001",

                "https://dev-api.tikim.org",
                "https://stg-api.tikim.org",
                "https://api.tikim.org",

                "https://dev.tikim.org",
                "https://stg.tikim.org",
                "https://www.tikim.org"
        };
    }
}
