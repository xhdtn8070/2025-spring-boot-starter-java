package org.tikim.sample.domain.board.reply.exception;


import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;
import org.tikim.sample.global.exception.exceptions.BaseException;

public class ReplyException extends BaseException {
    public ReplyException(ErrorMessage errorMessage, CriticalLevel criticalLevel) {
        super(errorMessage, criticalLevel);
    }

    public ReplyException(ErrorMessage errorMessage, CriticalLevel criticalLevel, String additionalInfo) {
        super(errorMessage, criticalLevel, additionalInfo);
    }
}
