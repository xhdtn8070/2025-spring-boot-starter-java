package org.tikim.sample.global.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    UNDEFINED_EXCEPTION("정의되지 않은 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PARAMETER_IS_NOT_CORRECT("파라미터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST("존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST),
    POST_NOT_EXIST("존재하지 않는 게시글입니다.", HttpStatus.BAD_REQUEST),
    REPLY_NOT_EXIST("존재하지 않는 댓글입니다.", HttpStatus.BAD_REQUEST),
    WRITER_NOT_MATCH("작성자가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    INVALID_AUTHORIZATION_HEADER("Authorization 헤더가 올바르지 않습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String errorMessage;
    private final HttpStatus httpStatus;
}
