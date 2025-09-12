// OpenApiConfig.java
package org.tikim.sample.global.docs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.tikim.sample.global.auth.dto.LoggedInUser;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "AuthorizationBearer";

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearer = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT") // 표기용 텍스트라서 아무거나 가능
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        return new OpenAPI()
            .info(new Info().title("API").version("1.0"))
            .components(new Components().addSecuritySchemes(SECURITY_SCHEME, bearer));
        // 전역 적용을 원하면 .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
    }

    @Bean
    public OperationCustomizer addAuthWhenLoggedInUserParam() {
        return (operation, handlerMethod) -> {
            boolean needsAuth = false;
            for (MethodParameter p : handlerMethod.getMethodParameters()) {
                if (p.hasParameterAnnotation(LoggedInUser.class)) {
                    needsAuth = true;
                    break;
                }
            }
            if (needsAuth) {
                operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
            }
            return operation;
        };
    }
}
