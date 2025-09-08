package org.tikim.sample.global.exception.handler;


import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.exceptions.BaseException;
import org.tikim.sample.global.exception.exceptions.UndefinedException;
import org.tikim.sample.global.response.dto.ApiResponse;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Set<String> exceptionSet = new HashSet<>();

    @PostConstruct
    public void init() {
        exceptionSet.add("NoHandlerFoundException");
        exceptionSet.add("HttpRequestMethodNotSupportedException");
        exceptionSet.add("DateTimeParseException");
        exceptionSet.add("MethodArgumentTypeMismatchException");
        exceptionSet.add("ConstraintViolationException");
        exceptionSet.add("ClientAbortException");
        exceptionSet.add("WebClientResponseException");
        exceptionSet.add("BadRequest");
        exceptionSet.add("IOException");
        exceptionSet.add("HttpMediaTypeNotAcceptableException");
        exceptionSet.add("MissingServletRequestParameterException");
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse<BaseException>> handleDefaultException(Throwable e) {
        BaseException baseException = determineBaseException(e);
        return ApiResponse.toResponseEntity(
            ApiResponse.error(baseException, baseException.getHttpStatus())
        );
    }

    private BaseException determineBaseException(Throwable e) {
        if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            UndefinedException baseException = new UndefinedException(e);
            if (exceptionSet.contains(baseException.getClassName())) {
                baseException.setCriticalLevel(CriticalLevel.NON_CRITICAL);
            }
            return baseException;
        }
    }
}
