package org.tikim.sample.global.exception.exceptions;


import org.springframework.http.HttpStatus;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;

public class UndefinedException extends BaseException {

    private static final ErrorMessage errorMessageEnum = ErrorMessage.UNDEFINED_EXCEPTION;
    private final Throwable e;

    public UndefinedException(Throwable e) {
        this(e, CriticalLevel.CRITICAL, null);
    }

    public UndefinedException(Throwable e, CriticalLevel criticalLevel) {
        this(e, criticalLevel, null);
    }

    public UndefinedException(Throwable e, CriticalLevel criticalLevel, String additionalInfo) {
        super(errorMessageEnum.getErrorMessage(), criticalLevel, HttpStatus.BAD_REQUEST, additionalInfo);
        this.className = e.getClass().getSimpleName();
        this.message = errorMessageEnum.getErrorMessage();
        this.code = errorMessageEnum.name();
        this.criticalLevel = criticalLevel;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.e = e;
    }

    @Override
    public String getErrorName() {
        return this.e.getClass().getSimpleName();
    }

    @Override
    public String getErrorFile() {
        return this.e.getStackTrace()[0].getFileName();
    }

    @Override
    public String getErrorMessage() {
        return this.e.getMessage();
    }

    @Override
    public int getErrorLine() {
        return this.e.getStackTrace()[0].getLineNumber();
    }

    @Override
    public String getSingleTrace() {
        return this.e.getStackTrace()[0].toString();
    }
}
