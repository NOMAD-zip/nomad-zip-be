package com.nz.nomadzip.user.exception;

import com.nz.nomadzip.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum UserErrorCode implements ErrorCode {
    /* ????? 에러코드 할당 */
    USER_NOT_FOUND("10007", "회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
