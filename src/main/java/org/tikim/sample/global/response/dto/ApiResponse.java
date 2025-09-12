package org.tikim.sample.global.response.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class ApiResponse<T> {
    private boolean isSuccess;
    private T data;
    private int code;

    private ApiResponse(boolean isSuccess, T data, int code) {
        this.isSuccess = isSuccess;
        this.data = data;
        this.code = code;
    }

    public static <T> ApiResponse<T> success(T data, HttpStatus httpStatus) {
        return new ApiResponse<>(true, data, httpStatus.value());
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, HttpStatus.OK.value());
    }

    public static ApiResponse<Void> success(HttpStatus httpStatus) {
        return new ApiResponse<>(true, null, httpStatus.value());
    }

    public static <T> ApiResponse<T> error(T data, HttpStatus httpStatus) {
        return new ApiResponse<>(false, data, httpStatus.value());
    }

    public static ApiResponse<Void> error(HttpStatus httpStatus) {
        return new ApiResponse<>(false, null, httpStatus.value());
    }

    public static <T> ResponseEntity<ApiResponse<T>> toResponseEntity(ApiResponse<T> apiResponse) {
        return ResponseEntity.status(HttpStatus.valueOf(apiResponse.getCode())).body(apiResponse);
    }
}
