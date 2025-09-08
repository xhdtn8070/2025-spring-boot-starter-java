package org.tikim.sample.global.docs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tikim.sample.global.docs.properties.OpenApiProperties;

@Configuration(proxyBeanMethods = false)
@ConfigurationPropertiesScan(basePackages = "org.tikim.sample.global.docs.properties")
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(OpenApiProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.getTitle())
                        .version(properties.getVersion())
                        .description(properties.getDescription()));
    }
}
