package org.tikim.sample.global.exception.exceptions;


import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;

public class CommonException extends BaseException {
    public CommonException(ErrorMessage errorMessage, CriticalLevel criticalLevel) {
        super(errorMessage, criticalLevel);
    }

    public CommonException(ErrorMessage errorMessage, CriticalLevel criticalLevel, String additionalInfo) {
        super(errorMessage, criticalLevel, additionalInfo);
    }
}
