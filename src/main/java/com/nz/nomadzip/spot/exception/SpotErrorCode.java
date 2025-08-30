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
    DUPLICATE_DATA_ERROR("90001", "같은 데이터가 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    ERROR_CODE("에러 코드 입력", "에러 메시지 입력", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
