package com.nz.nomadzip.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/* 전역 오류 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum GlobalErrorCode implements ErrorCode{

    // 그 외 기타 오류
    INTERNAL_SERVER_ERROR("10001", "내부 서버 오류 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}