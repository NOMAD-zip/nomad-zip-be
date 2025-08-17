package com.nz.nomadzip.spot.exception;

import com.nz.nomadzip.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum SpotErrorCode implements ErrorCode {
    /* ????? 에러코드 할당 */
    ERROR_CODE("에러 코드 입력", "에러 메시지 입력", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
