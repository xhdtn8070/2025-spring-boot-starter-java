// LoggedInUser.java
package org.tikim.sample.global.auth.dto;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Parameter(hidden = true)   // ← Swagger에 파라미터로 노출되지 않게 숨김
public @interface LoggedInUser {
    boolean required() default true;
}
