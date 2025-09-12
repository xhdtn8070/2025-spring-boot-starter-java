// LoggedInUserArgumentResolver.java
package org.tikim.sample.global.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.tikim.sample.global.auth.dto.LoggedInUser;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;
import org.tikim.sample.global.exception.exceptions.CommonException;

@Component
public class LoggedInUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean annotated = parameter.hasParameterAnnotation(LoggedInUser.class);
        Class<?> type = parameter.getParameterType();
        boolean longType = (type == Long.class || type == long.class);
        return annotated && longType;
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        String header = webRequest.getHeader("Authorization");
        LoggedInUser ann = parameter.getParameterAnnotation(LoggedInUser.class);
        boolean required = ann == null || ann.required();

        if (header == null || header.isBlank()) {
            if (required) {
                throw new CommonException(ErrorMessage.INVALID_AUTHORIZATION_HEADER, CriticalLevel.NON_CRITICAL);
            }
            return (parameter.getParameterType() == long.class) ? 0L : null;
        }

        String token = header.trim();
        // 혹시 "Bearer 123"처럼 들어와도 대응 (공백 기준 두 토큰이면 두번째를 사용)
        if (token.regionMatches(true, 0, "Bearer", 0, 6)) {
            String[] parts = token.split("\\s+", 2);
            token = (parts.length == 2) ? parts[1] : "";
        }

        try {
            Long userId = Long.parseLong(token);
            return (parameter.getParameterType() == long.class) ? userId.longValue() : userId;
        } catch (NumberFormatException e) {
            throw new CommonException(ErrorMessage.INVALID_AUTHORIZATION_HEADER, CriticalLevel.NON_CRITICAL);
        }
    }
}
