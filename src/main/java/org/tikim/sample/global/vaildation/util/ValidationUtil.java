package org.tikim.sample.global.vaildation.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.function.Consumer;

public class ValidationUtil {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    // 기본 validate 메서드
    public static <T> void validate(T object, Consumer<Set<ConstraintViolation<T>>> onError) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        if (!constraintViolations.isEmpty()) {
            onError.accept(constraintViolations);
        }
    }

    // 그룹을 지원하는 validate 메서드
    public static <T> void validate(T object, Consumer<Set<ConstraintViolation<T>>> onError, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            onError.accept(constraintViolations);
        }
    }

    // ConstraintViolationException을 자동으로 throw하는 기본 validate
    public static <T> void validate(T object) {
        validate(object, constraintViolations -> {
            throw new ConstraintViolationException(constraintViolations);
        });
    }

    // 그룹도 지원하는 버전
    public static <T> void validate(T object, Class<?>... groups) {
        validate(object, constraintViolations -> {
            throw new ConstraintViolationException(constraintViolations);
        }, groups);
    }

    /**
     * 특정 필드의 값을 원하는 타입으로 검증하는 함수. 예상한 타입과 실제 타입이 다를 경우 예외를 던지며, isNullable이 false일 경우 null 값도 예외를 던짐.
     *
     * @param <T>        예상하는 타입
     * @param value      검증할 값
     * @param fieldName  필드 이름 (예외 메시지에 포함됨)
     * @param clazz      예상하는 클래스 타입 (예: String.class)
     * @param isNullable null 허용 여부 (false일 경우 null 값이 들어오면 예외 발생)
     * @return 예상한 타입으로 캐스팅된 값
     * @throws IllegalArgumentException 예상한 타입이 아니거나 null일 경우 예외 발생
     */
    public static <T> T validateType(Object value, String fieldName, Class<T> clazz, boolean isNullable) {
        if (value == null) {
            if (!isNullable) {
                throw new IllegalArgumentException(
                    String.format("Field '%s' cannot be null. Expected type: %s", fieldName, clazz.getName()));
            }
            return null; // null 허용 시 그대로 반환
        }

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            throw new IllegalArgumentException(
                String.format("Unexpected type for field '%s': Expected %s, but received %s",
                    fieldName, clazz.getName(), value.getClass().getName()));
        }
    }

    /**
     * 특정 필드의 값을 null 허용하지 않고 원하는 타입으로 검증하는 함수 (기본 동작).
     *
     * @param <T>       예상하는 타입
     * @param value     검증할 값
     * @param fieldName 필드 이름 (예외 메시지에 포함됨)
     * @param clazz     예상하는 클래스 타입 (예: String.class)
     * @return 예상한 타입으로 캐스팅된 값
     * @throws IllegalArgumentException 예상한 타입이 아니거나 null일 경우 예외 발생
     */
    public static <T> T validateType(Object value, String fieldName, Class<T> clazz) {
        return validateType(value, fieldName, clazz, false); // 기본값으로 null 허용하지 않음
    }

}
