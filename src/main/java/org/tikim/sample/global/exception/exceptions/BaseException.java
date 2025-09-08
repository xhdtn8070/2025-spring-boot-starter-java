package org.tikim.sample.global.exception.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;
import org.tikim.sample.global.exception.interfaces.ErrorInfo;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "cause", "stackTrace","message","localizedMessage","suppressed",
    "httpStatus", "errorName", "errorLine", "errorFile" })
public abstract class BaseException extends RuntimeException implements ErrorInfo {
    protected String className;
    protected String message;
    protected String code;
    protected CriticalLevel criticalLevel;
    protected HttpStatus httpStatus;
    protected String additionalInfo;

    protected BaseException(ErrorMessage errorMessage, CriticalLevel criticalLevel) {
        this(errorMessage, criticalLevel, null);
    }

    protected BaseException(ErrorMessage errorMessage, CriticalLevel criticalLevel, String additionalInfo) {
        this.className = this.getClass().getSimpleName();
        this.message = errorMessage.getErrorMessage();
        this.code = errorMessage.name();
        this.criticalLevel = criticalLevel;
        this.httpStatus = errorMessage.getHttpStatus();
        this.additionalInfo = additionalInfo;
    }

    protected BaseException(String errorMessage, CriticalLevel criticalLevel, HttpStatus httpStatus) {
        this(errorMessage, criticalLevel, httpStatus, null);
    }

    protected BaseException(String errorMessage, CriticalLevel criticalLevel, HttpStatus httpStatus, String additionalInfo) {
        this.className = this.getClass().getSimpleName();
        this.message = errorMessage;
        this.criticalLevel = criticalLevel;
        this.httpStatus = httpStatus;
        this.additionalInfo = additionalInfo;
    }

    protected BaseException() {
        this.className = this.getClass().getSimpleName();
    }

    @Override
    public String getErrorName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getErrorFile() {
        return this.getStackTrace()[0].getFileName();
    }

    @Override
    public String getErrorMessage() {
        return this.message;
    }

    @Override
    public String getSingleTrace() {
        return this.getStackTrace()[0].toString();
    }

    @Override
    public int getErrorLine() {
        return this.getStackTrace()[0].getLineNumber();
    }
}
