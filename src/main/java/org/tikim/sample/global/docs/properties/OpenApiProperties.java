package org.tikim.sample.global.docs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "openapi.info")
public class OpenApiProperties {
    private String title;
    private String version;
    private String description;
}
